package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.common.ValidationUtil;
import com.yaozhidao.dto.response.HospitalDetailResponse;
import com.yaozhidao.dto.response.HospitalListResponse;
import com.yaozhidao.entity.Hospital;
import com.yaozhidao.entity.HospitalDepartment;
import com.yaozhidao.mapper.HospitalDepartmentMapper;
import com.yaozhidao.mapper.HospitalMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 就诊导航服务
 * 推荐算法（PRD 4.3.2）：综合得分 = 距离×0.40 + 科室匹配×0.35 + 交通×0.15 + 口碑×0.10
 * 距离用 Haversine 公式在内存计算（种子量级足够；数据量大后应下推 SQL 或地理索引）
 */
@Service
public class HospitalService {

    /** 交通系数：开发模式无实时路况 API，固定 0.5（PRD 降级规则） */
    private static final double TRAFFIC_FACTOR = 0.5;

    private final HospitalMapper hospitalMapper;
    private final HospitalDepartmentMapper departmentMapper;

    public HospitalService(HospitalMapper hospitalMapper, HospitalDepartmentMapper departmentMapper) {
        this.hospitalMapper = hospitalMapper;
        this.departmentMapper = departmentMapper;
    }

    /** 紧急就诊推荐 */
    public HospitalListResponse recommend(Double longitude, Double latitude,
                                          String department, Integer distance,
                                          Integer page, Integer size) {
        if (longitude == null || latitude == null
                || longitude < -180 || longitude > 180 || latitude < -90 || latitude > 90) {
            throw new BizException(ResultCode.LOCATION_INVALID);
        }
        // 半径：默认 10km，上限 30km
        double radius = 10;
        if (distance != null) {
            if (distance <= 0 || distance > 30) {
                throw new BizException(ResultCode.DISTANCE_INVALID);
            }
            radius = distance;
        }
        int[] p = ValidationUtil.normalizePage(page, size);

        // 全量医院 + 各自强项科室（内存计算，种子量级）
        List<Hospital> all = hospitalMapper.findAll();
        Map<Long, List<String>> mainDepts = new HashMap<>();
        for (Hospital h : all) {
            mainDepts.put(h.getId(), mainDepts(h.getId()));
        }

        // 1. 算距离 + 半径过滤
        List<Candidate> candidates = new ArrayList<>();
        for (Hospital h : all) {
            double d = haversineKm(longitude, latitude, h.getLongitude().doubleValue(), h.getLatitude().doubleValue());
            if (d <= radius) {
                candidates.add(new Candidate(h, d));
            }
        }
        if (candidates.isEmpty()) {
            // 10 公里内为空：扩大至 30 公里再试（PRD 规则）
            radius = 30;
            for (Hospital h : all) {
                double d = haversineKm(longitude, latitude, h.getLongitude().doubleValue(), h.getLatitude().doubleValue());
                if (d <= radius) {
                    candidates.add(new Candidate(h, d));
                }
            }
        }

        // 2. 四项分量打分
        double minDist = candidates.stream().mapToDouble(c -> c.distance).min().orElse(0);
        for (Candidate c : candidates) {
            // 距离分：最近医院 100 分，每远 1km 扣 5 分，最低 0
            double distScore = Math.max(0, 100 - 5 * (c.distance - minDist));
            // 科室匹配：传了科室且该院有该科室 → 100，否则 0
            double deptScore = 0;
            if (department != null && !department.isBlank()) {
                List<String> depts = mainDepts.getOrDefault(c.hospital.getId(), List.of());
                if (depts.stream().anyMatch(d -> d.contains(department) || department.contains(d))) {
                    deptScore = 100;
                }
            }
            // 交通：开发模式固定 0.5 → 50 分
            double trafficScore = TRAFFIC_FACTOR * 100;
            // 口碑：rating/5×100
            double ratingScore = c.hospital.getRating().doubleValue() / 5 * 100;

            c.score = distScore * 0.40 + deptScore * 0.35 + trafficScore * 0.15 + ratingScore * 0.10;
        }

        // 3. 按得分降序 + 分页
        candidates.sort(Comparator.comparingDouble((Candidate c) -> c.score).reversed()
                .thenComparing(c -> c.distance));
        long total = candidates.size();
        int from = Math.min((p[0] - 1) * p[1], candidates.size());
        int to = Math.min(from + p[1], candidates.size());
        List<Candidate> pageList = candidates.subList(from, to);

        HospitalListResponse resp = new HospitalListResponse();
        resp.setTotal(total);
        resp.setPage(p[0]);
        resp.setSize(p[1]);
        resp.setList(pageList.stream().map(c -> {
            HospitalListResponse.ListItem item = new HospitalListResponse.ListItem();
            item.setHospitalId(c.hospital.getId());
            item.setName(c.hospital.getName());
            item.setAddress(c.hospital.getAddress());
            item.setDistance(Math.round(c.distance * 10) / 10.0);
            item.setRating(c.hospital.getRating().doubleValue());
            item.setMainDepartments(mainDepts.get(c.hospital.getId()));
            item.setEstimatedTime((int) Math.round(c.distance / 30 * 60)); // 30km/h 估算分钟
            item.setPhone(c.hospital.getPhone());
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 医院详情（含科室列表） */
    public HospitalDetailResponse getDetail(Long hospitalId) {
        Hospital h = hospitalMapper.findById(hospitalId);
        if (h == null) {
            throw new BizException(ResultCode.HOSPITAL_NOT_FOUND);
        }
        HospitalDetailResponse resp = new HospitalDetailResponse();
        resp.setHospitalId(h.getId());
        resp.setName(h.getName());
        resp.setAddress(h.getAddress());
        resp.setPhone(h.getPhone());
        resp.setRating(h.getRating().doubleValue());
        resp.setIntroduction(h.getIntroduction());
        resp.setDepartments(departmentMapper.findByHospitalId(hospitalId).stream()
                .map(d -> new HospitalDetailResponse.DepartmentItem(d.getDepartmentName(), d.getDescription()))
                .collect(Collectors.toList()));
        return resp;
    }

    /** 搜索医院（名称/地址模糊） */
    public HospitalListResponse search(String keyword, Integer page, Integer size) {
        String err = ValidationUtil.validateKeyword(keyword);
        if (err != null) {
            // 文档约定：关键词为空 40001，含不规范字符 40002
            throw new BizException(err.contains("不能为空") ? ResultCode.PARAM_COMMON : ResultCode.PARAM_DATE, err);
        }
        int[] p = ValidationUtil.normalizePage(page, size);
        String kw = keyword.trim();

        List<Hospital> list = hospitalMapper.search(kw, (p[0] - 1) * p[1], p[1]);
        long total = hospitalMapper.countSearch(kw);

        HospitalListResponse resp = new HospitalListResponse();
        resp.setTotal(total);
        resp.setPage(p[0]);
        resp.setSize(p[1]);
        resp.setList(list.stream().map(h -> {
            HospitalListResponse.ListItem item = new HospitalListResponse.ListItem();
            item.setHospitalId(h.getId());
            item.setName(h.getName());
            item.setAddress(h.getAddress());
            item.setPhone(h.getPhone());
            item.setRating(h.getRating().doubleValue());
            item.setMainDepartments(mainDepts(h.getId()));
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 强项科室名列表（is_main=1） */
    private List<String> mainDepts(Long hospitalId) {
        return departmentMapper.findByHospitalId(hospitalId).stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsMain()))
                .map(HospitalDepartment::getDepartmentName)
                .collect(Collectors.toList());
    }

    /** Haversine 公式计算两经纬度间的公里数 */
    private double haversineKm(double lon1, double lat1, double lon2, double lat2) {
        double r = 6371; // 地球半径 km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * r * Math.asin(Math.sqrt(a));
    }

    /** 推荐候选（医院 + 距离 + 得分） */
    private static class Candidate {
        final Hospital hospital;
        final double distance;
        double score;

        Candidate(Hospital hospital, double distance) {
            this.hospital = hospital;
            this.distance = distance;
        }
    }
}

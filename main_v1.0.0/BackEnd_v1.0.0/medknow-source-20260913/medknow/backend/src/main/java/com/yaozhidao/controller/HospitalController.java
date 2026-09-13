package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.response.HospitalDetailResponse;
import com.yaozhidao.dto.response.HospitalListResponse;
import com.yaozhidao.service.HospitalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 就诊导航：推荐/详情/搜索（公开接口，Android 端负责定位） */
@RestController
@RequestMapping("/api/v1/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    /** GET /api/v1/hospitals/recommend 紧急就诊推荐（综合得分排序） */
    @GetMapping("/recommend")
    public Result<HospitalListResponse> recommend(@RequestParam(required = false) Double longitude,
                                                  @RequestParam(required = false) Double latitude,
                                                  @RequestParam(required = false) String department,
                                                  @RequestParam(required = false) Integer distance,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer size) {
        return Result.success("推荐成功", hospitalService.recommend(longitude, latitude, department, distance, page, size));
    }

    /** GET /api/v1/hospitals/{hospitalsId} 医院详情 */
    @GetMapping("/{hospitalsId}")
    public Result<HospitalDetailResponse> detail(@PathVariable Long hospitalsId) {
        return Result.success("获取成功", hospitalService.getDetail(hospitalsId));
    }

    /** GET /api/v1/hospitals/search 搜索医院（名称/地址） */
    @GetMapping("/search")
    public Result<HospitalListResponse> search(@RequestParam String keyword,
                                               @RequestParam(required = false) Integer page,
                                               @RequestParam(required = false) Integer size) {
        return Result.success("搜索成功", hospitalService.search(keyword, page, size));
    }
}

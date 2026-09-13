package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.common.ValidationUtil;
import com.yaozhidao.dto.response.DrugSearchResponse;
import com.yaozhidao.entity.Drug;
import com.yaozhidao.mapper.DrugMapper;
import com.yaozhidao.mapper.UserDrugBoxMapper;
import com.yaozhidao.security.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** 药品搜索 / 详情 / 药箱收藏 */
@Service
public class DrugService {

    private final DrugMapper drugMapper;
    private final UserDrugBoxMapper userDrugBoxMapper;

    public DrugService(DrugMapper drugMapper, UserDrugBoxMapper userDrugBoxMapper) {
        this.drugMapper = drugMapper;
        this.userDrugBoxMapper = userDrugBoxMapper;
    }

    /** 模糊搜索（公开接口，未登录可用） */
    public DrugSearchResponse search(String keyword, Integer page, Integer size) {
        String err = ValidationUtil.validateKeyword(keyword);
        if (err != null) {
            // 文档约定：关键词为空 40001，含不规范字符 40002
            throw new BizException(err.contains("不能为空") ? ResultCode.PARAM_COMMON : ResultCode.PARAM_DATE, err);
        }
        int[] p = ValidationUtil.normalizePage(page, size);

        String kw = keyword.trim();
        List<Drug> list = drugMapper.search(kw, (p[0] - 1) * p[1], p[1]);
        long total = drugMapper.countSearch(kw);

        DrugSearchResponse resp = new DrugSearchResponse();
        resp.setTotal(total);
        resp.setPage(p[0]);
        resp.setSize(p[1]);
        resp.setResults(list.stream().map(d -> {
            DrugSearchResponse.SearchItem item = new DrugSearchResponse.SearchItem();
            item.setDrugId(d.getId());
            item.setDrugName(d.getDrugName());
            item.setGenericName(d.getGenericName());
            item.setCategory(d.getCategory());
            item.setSummary(d.getSummary());
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 药品详情（公开） */
    public Drug getDetail(Long drugId) {
        Drug drug = drugMapper.findById(drugId);
        if (drug == null) {
            throw new BizException(ResultCode.DRUG_NOT_FOUND);
        }
        return drug;
    }

    /** 收藏到药箱（需登录，重复收藏 40905） */
    public void collect(Long drugId) {
        Long userId = UserContext.requireUserId();
        if (drugMapper.findById(drugId) == null) {
            throw new BizException(ResultCode.DRUG_NOT_FOUND);
        }
        if (userDrugBoxMapper.count(userId, drugId) > 0) {
            throw new BizException(ResultCode.DRUG_ALREADY_IN_BOX);
        }
        userDrugBoxMapper.insert(userId, drugId);
    }
}

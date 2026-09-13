package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.response.DrugSearchResponse;
import com.yaozhidao.entity.Drug;
import com.yaozhidao.service.DrugService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 药品搜索（公开）与药箱收藏（需登录） */
@RestController
@RequestMapping("/api/v1/drugs")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    /** GET /api/v1/drugs/search 药品模糊搜索（公开，未登录可用） */
    @GetMapping("/search")
    public Result<DrugSearchResponse> search(@RequestParam String keyword,
                                             @RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        return Result.success("搜索成功", drugService.search(keyword, page, size));
    }

    /** GET /api/v1/drugs/{drugId} 药品详情（公开） */
    @GetMapping("/{drugId}")
    public Result<Drug> detail(@PathVariable Long drugId) {
        return Result.success("获取成功", drugService.getDetail(drugId));
    }

    /** POST /api/v1/drugs/{drugId}/collect 收藏到药箱（需登录，重复 40905） */
    @PostMapping("/{drugId}/collect")
    public Result<Void> collect(@PathVariable Long drugId) {
        drugService.collect(drugId);
        return Result.success("已收藏", null);
    }
}

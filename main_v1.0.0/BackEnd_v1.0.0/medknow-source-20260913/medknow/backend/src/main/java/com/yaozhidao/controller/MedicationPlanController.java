package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.PlanUpsertRequest;
import com.yaozhidao.dto.response.PlanCreateResponse;
import com.yaozhidao.dto.response.PlanDetailResponse;
import com.yaozhidao.dto.response.PlanListResponse;
import com.yaozhidao.dto.response.PlanStateResponse;
import com.yaozhidao.service.MedicationPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 用药计划：创建/列表/详情/更新/激活/暂停/恢复（需要登录） */
@RestController
@RequestMapping("/api/v1/medication-plans")
public class MedicationPlanController {

    private final MedicationPlanService planService;

    public MedicationPlanController(MedicationPlanService planService) {
        this.planService = planService;
    }

    /** POST /api/v1/medication-plans 创建用药计划（草稿） */
    @PostMapping
    public ResponseEntity<Result<PlanCreateResponse>> create(@Valid @RequestBody PlanUpsertRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Result<>(0, "创建成功", planService.create(req)));
    }

    /** GET /api/v1/medication-plans 计划列表（支持状态筛选 + 分页） */
    @GetMapping
    public Result<PlanListResponse> list(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size) {
        return Result.success(planService.list(status, page, size));
    }

    /** GET /api/v1/medication-plans/{planId} 计划详情（含今日提醒） */
    @GetMapping("/{planId}")
    public Result<PlanDetailResponse> detail(@PathVariable Long planId) {
        return Result.success(planService.detail(planId));
    }

    /** PUT /api/v1/medication-plans/{planId} 更新计划（仅草稿/暂停） */
    @PutMapping("/{planId}")
    public Result<Void> update(@PathVariable Long planId, @Valid @RequestBody PlanUpsertRequest req) {
        planService.update(planId, req);
        return Result.success("更新成功", null);
    }

    /** POST /{planId}/activate 激活（批量生成提醒） */
    @PostMapping("/{planId}/activate")
    public Result<PlanStateResponse> activate(@PathVariable Long planId) {
        return Result.success("已生效", planService.activate(planId));
    }

    /** POST /{planId}/pause 暂停（仅生效中） */
    @PostMapping("/{planId}/pause")
    public Result<PlanStateResponse> pause(@PathVariable Long planId) {
        return Result.success("已暂停", planService.pause(planId));
    }

    /** POST /{planId}/resume 恢复（仅暂停中） */
    @PostMapping("/{planId}/resume")
    public Result<PlanStateResponse> resume(@PathVariable Long planId) {
        return Result.success("已恢复", planService.resume(planId));
    }
}

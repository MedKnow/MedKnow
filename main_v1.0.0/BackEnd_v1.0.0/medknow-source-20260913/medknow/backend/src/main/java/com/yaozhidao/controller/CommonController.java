package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.FeedbackRequest;
import com.yaozhidao.service.CommonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/** 通用：健康检查（独立格式）+ 用户反馈 */
@RestController
@RequestMapping("/api/v1")
public class CommonController {

    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    /**
     * GET /api/v1/health 健康检查
     * 注意：独立响应格式 {status, timestamp}，不走统一包装（API 文档约定）
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> resp = new LinkedHashMap<>();
        resp.put("status", "ok");
        resp.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return resp;
    }

    /** POST /api/v1/feedback 用户反馈（登录可选，匿名可提交） */
    @PostMapping("/feedback")
    public Result<Void> feedback(@RequestBody FeedbackRequest req) {
        commonService.submitFeedback(req);
        return Result.success("提交成功", null);
    }
}

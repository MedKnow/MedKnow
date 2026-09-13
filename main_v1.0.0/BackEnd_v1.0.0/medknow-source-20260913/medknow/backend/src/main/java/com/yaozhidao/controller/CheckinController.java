package com.yaozhidao.controller;

import com.yaozhidao.common.Result;
import com.yaozhidao.dto.request.CheckinRequest;
import com.yaozhidao.dto.response.StatsResponse;
import com.yaozhidao.dto.response.TodayRemindersResponse;
import com.yaozhidao.service.CheckinService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 服药打卡：打卡 / 今日列表 / 依从率统计（需要登录） */
@RestController
@RequestMapping("/api/v1/checkins")
public class CheckinController {

    private final CheckinService checkinService;

    public CheckinController(CheckinService checkinService) {
        this.checkinService = checkinService;
    }

    /** POST /api/v1/checkins 服药打卡 */
    @PostMapping
    public Result<Void> checkin(@Valid @RequestBody CheckinRequest req) {
        checkinService.checkin(req);
        return Result.success("打卡成功", null);
    }

    /** GET /api/v1/checkins/today 今日待打卡列表 */
    @GetMapping("/today")
    public Result<TodayRemindersResponse> today() {
        return Result.success("获取成功", checkinService.today());
    }

    /** GET /api/v1/checkins/stats 依从率统计（period=WEEK|MONTH） */
    @GetMapping("/stats")
    public Result<StatsResponse> stats(@RequestParam(required = false, defaultValue = "WEEK") String period) {
        return Result.success("获取成功", checkinService.stats(period));
    }
}

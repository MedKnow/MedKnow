package com.yaozhidao.service;

import com.yaozhidao.common.BizException;
import com.yaozhidao.common.ResultCode;
import com.yaozhidao.dto.request.CheckinRequest;
import com.yaozhidao.dto.response.StatsResponse;
import com.yaozhidao.dto.response.TodayRemindersResponse;
import com.yaozhidao.entity.Reminder;
import com.yaozhidao.mapper.ReminderMapper;
import com.yaozhidao.security.UserContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服药打卡服务
 * 提醒状态机采用"懒更新"：查询前先执行状态翻转 SQL（PENDING→TRIGGERED→EXPIRED），
 * 定时任务 ReminderScheduler 每分钟兜底，双保险
 */
@Service
public class CheckinService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReminderMapper reminderMapper;

    public CheckinService(ReminderMapper reminderMapper) {
        this.reminderMapper = reminderMapper;
    }

    /** 服药打卡（幂等：已打卡返回 40901） */
    public void checkin(CheckinRequest req) {
        Long userId = UserContext.requireUserId();
        Reminder reminder = reminderMapper.findById(req.getReminderId());
        if (reminder == null) {
            throw new BizException(ResultCode.REMINDER_NOT_FOUND);
        }
        if (!reminder.getUserId().equals(userId)) {
            throw new BizException(ResultCode.FORBIDDEN_PLAN);
        }

        LocalDateTime actualTime;
        try {
            actualTime = LocalDateTime.parse(req.getActualTime(), DT_FMT);
        } catch (DateTimeParseException e) {
            throw new BizException(ResultCode.PARAM_COMMON, "实际服药时间格式应为 yyyy-MM-dd HH:mm");
        }

        // WHERE status<>'TAKEN'：并发双打卡第二条影响 0 行 → 409
        int rows = reminderMapper.checkin(req.getReminderId(), actualTime, Boolean.TRUE.equals(req.getIsLate()));
        if (rows == 0) {
            throw new BizException(ResultCode.STATE_CONFLICT, "该提醒已打卡");
        }
    }

    /** 今日待打卡列表（查询前先做状态懒翻转） */
    public TodayRemindersResponse today() {
        Long userId = UserContext.requireUserId();
        reminderMapper.triggerDueReminders();
        reminderMapper.expireOverdueReminders();

        TodayRemindersResponse resp = new TodayRemindersResponse();
        resp.setReminders(reminderMapper.findByUserAndDate(userId, LocalDate.now()).stream().map(r -> {
            TodayRemindersResponse.Item item = new TodayRemindersResponse.Item();
            item.setReminderId(r.getId());
            item.setPlanId(r.getPlanId());
            item.setDrugName(r.getDrugName());
            item.setScheduledTime(r.getScheduledTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            item.setStatus(r.getStatus());
            return item;
        }).collect(Collectors.toList()));
        return resp;
    }

    /** 依从率统计：WEEK=近7天 / MONTH=近30天（滚动窗口；PRD 自然周口径差异见 README） */
    public StatsResponse stats(String period) {
        Long userId = UserContext.requireUserId();
        if (!"WEEK".equals(period) && !"MONTH".equals(period)) {
            throw new BizException(ResultCode.PARAM_VALUE, "无效的统计周期，仅支持 WEEK/MONTH");
        }

        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays("WEEK".equals(period) ? 7 : 30);
        Map<String, Object> stat = reminderMapper.stats(userId, start, end);

        long total = ((Number) stat.getOrDefault("totalDoses", 0)).longValue();
        long confirmed = ((Number) stat.getOrDefault("confirmedDoses", 0)).longValue();
        long missed = ((Number) stat.getOrDefault("missedDoses", 0)).longValue();

        StatsResponse resp = new StatsResponse();
        resp.setPeriod(period);
        resp.setTotalDoses(total);
        resp.setConfirmedDoses(confirmed);
        resp.setMissedDoses(missed);
        resp.setAdherenceRate(total == 0 ? 0.0
                : Math.round(confirmed * 1000.0 / total) / 10.0); // 保留一位小数
        return resp;
    }
}

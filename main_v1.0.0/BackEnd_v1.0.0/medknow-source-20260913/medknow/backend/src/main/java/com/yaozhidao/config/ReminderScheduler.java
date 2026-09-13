package com.yaozhidao.config;

import com.yaozhidao.mapper.MedicationPlanMapper;
import com.yaozhidao.mapper.ReminderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 提醒状态兜底定时任务：每分钟执行一次状态翻转
 * 作用：即使没有任何接口被访问，提醒状态也能按时流转（PENDING→TRIGGERED→EXPIRED）
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final ReminderMapper reminderMapper;
    private final MedicationPlanMapper planMapper;

    public ReminderScheduler(ReminderMapper reminderMapper, MedicationPlanMapper planMapper) {
        this.reminderMapper = reminderMapper;
        this.planMapper = planMapper;
    }

    @Scheduled(fixedDelay = 60_000)
    public void flipReminderStatus() {
        try {
            int triggered = reminderMapper.triggerDueReminders();
            int expired = reminderMapper.expireOverdueReminders();
            int planExpired = planMapper.expireOverduePlans();
            if (triggered > 0 || expired > 0 || planExpired > 0) {
                log.info("定时任务状态翻转：触发 {} 条，过期 {} 条，计划到期 {} 条", triggered, expired, planExpired);
            }
        } catch (Exception e) {
            log.error("定时任务执行失败", e);
        }
    }
}

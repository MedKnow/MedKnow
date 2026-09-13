package com.yaozhidao.mapper;

import com.yaozhidao.entity.Reminder;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReminderMapper {

    /** 批量生成提醒（激活时调用），唯一键兜底幂等 */
    int insertBatch(@Param("list") List<Reminder> reminders);

    /** 某天某药某时间点是否已存在提醒（幂等判断） */
    int countByPlanDrugAndDate(@Param("planDrugId") Long planDrugId,
                               @Param("date") LocalDate date,
                               @Param("time") java.time.LocalTime time);

    Reminder findById(@Param("id") Long id);

    /** 某用户某天的提醒，按时间排序 */
    List<Reminder> findByUserAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /** 某计划某天的提醒 */
    List<Reminder> findByPlanAndDate(@Param("planId") Long planId, @Param("date") LocalDate date);

    /** 打卡：状态改为 TAKEN；WHERE 带 status<>'TAKEN' 用影响行数防并发双打卡 */
    int checkin(@Param("id") Long id,
                @Param("actualTime") LocalDateTime actualTime,
                @Param("isLate") boolean isLate);

    /** 懒更新：到点 PENDING→TRIGGERED（仅所属计划 ACTIVE 的提醒才触发） */
    int triggerDueReminders();

    /** 懒更新：超过30分钟未处理 PENDING/TRIGGERED→EXPIRED */
    int expireOverdueReminders();

    /** 依从率统计：周期内 total/confirmed/missed 计数 */
    Map<String, Object> stats(@Param("userId") Long userId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    /** 单计划依从率：total/confirmed 计数 */
    Map<String, Object> statsByPlan(@Param("planId") Long planId);
}

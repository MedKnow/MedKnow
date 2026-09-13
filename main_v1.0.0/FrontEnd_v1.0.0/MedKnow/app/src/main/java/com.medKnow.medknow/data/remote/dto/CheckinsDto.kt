package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.checkins.Reminders
import com.medKnow.medknow.model.checkins.getComplianceStatistics.Period

// 服药打卡请求体
data class CheckinsRequestDto(

    // 提醒ID。要打卡的提醒记录ID
    val reminderId: Int,

    // 实际服药时间。用户真实服药的时间点，匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$
    val actualTime: String,

    // 是否延迟打卡。超过提醒时间 30 分钟后打卡为 true
    val isLate: Boolean

)

// 今日待打卡列表 data
data class TodayCheckinsListData(
    // 今日提醒列表
    val reminders: List<Reminders>
)

// 提醒 dto
data class RemindersDto(

    // 提醒ID
    val reminderId: Int,

    // 所属计划ID
    val planId: Int,

    // 药品名称
    val drugName: String,

    // 计划服药时间。仅当天时间点，匹配模式：^([01]\d|2[0-3]):([0-5]\d)$
    val scheduledTime: String,

    // 提醒状态。当前提醒的状态
    var status: ReminderStatus

)

// 依从率统计 data
data class ComplianceStatisticsData(

    // 统计周期
    val period: Period,

    // 计划总次数。该周期内应服药总次数
    val totalDoses: Int,

    // 已确认次数。用户实际打卡次数
    val confirmedDoses: Int,

    // 漏服次数。超时未处理的提醒数
    val missedDoses: Int,

    // 依从率。百分比，保留一位小数
    val adherenceRate: Double

)
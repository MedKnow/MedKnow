package com.medKnow.medknow.model.medicationPlans.getPlanMessage

import com.medKnow.medknow.model.ReminderStatus

// 今日提醒。当天所有提醒
data class TodayReminders (

    // 提醒ID
    val reminderId: Int,

    // 药品名
    val drugName: String,

    // 计划提醒时间。匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$
    val scheduledTime: String,

    // 提醒状态
    var status: ReminderStatus,

)
package com.medKnow.medknow.model.checkins

import com.medKnow.medknow.model.ReminderStatus

// 今日提醒
data class Reminders (

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
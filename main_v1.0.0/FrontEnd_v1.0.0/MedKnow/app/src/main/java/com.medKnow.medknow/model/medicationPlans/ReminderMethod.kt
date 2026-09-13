package com.medKnow.medknow.model.medicationPlans

// 提醒方式
enum class ReminderMethod {

    // 闹钟提醒。本地系统闹钟，无需网络，必选提醒方式
    ALARM,

    // App推送。通过极光/Firebase等推送通道发送通知栏消息
    PUSH,

    // 短信提醒。通过短信网关发送手机短信，用户可选配
    SMS,

    // 未来可能新增的未知方式
    UNKNOWN
}
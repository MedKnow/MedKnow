package com.medKnow.medknow.model

// 提醒状态
enum class ReminderStatus {

    // 待提醒
    PENDING,

    // 已触发
    TRIGGERED,

    // 已服药/已处理
    TAKEN,

    // 已忽略
    IGNORED,

    // 已过期
    EXPIRED

}
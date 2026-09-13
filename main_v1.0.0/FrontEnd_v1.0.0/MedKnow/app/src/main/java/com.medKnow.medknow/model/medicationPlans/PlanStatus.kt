package com.medKnow.medknow.model.medicationPlans

// 计划状态
enum class PlanStatus {

    // 草稿
    DRAFT,

    // 进行中
    ACTIVE,

    // 已暂停
    PAUSED,

    // 已完成
    COMPLETED,

    // 已过期
    EXPIRED

}
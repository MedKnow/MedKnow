package com.medKnow.medknow.model.medicationPlans

// 恢复用药计划
data class ResumeMedicationPlanResponse (

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: PlanStatus,

    // 恢复时间
    val resumedAt: String

)
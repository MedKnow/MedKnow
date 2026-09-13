package com.medKnow.medknow.model.medicationPlans

// 暂停用药计划
data class StopMedicationPlanResponse (

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: PlanStatus,

    // 暂停时间
    val pausedAt: String

)
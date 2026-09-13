package com.medKnow.medknow.model.medicationPlans

// 激活用药计划
data class ActiveMedicationPlanResponse (

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: PlanStatus,

    // 激活时间。格式yyyy-MM--ddTHH:mm:ssZ
    val activatedAt: String

)
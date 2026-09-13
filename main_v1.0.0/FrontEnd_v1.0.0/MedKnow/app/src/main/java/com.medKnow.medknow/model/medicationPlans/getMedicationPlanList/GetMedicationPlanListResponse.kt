package com.medKnow.medknow.model.medicationPlans.getMedicationPlanList

// 获取用药计划列表（返回响应）
data class GetMedicationPlanListResponse (

    // 总记录数
    val total: Int,

    // 当前页码
    val pageNum: Int,

    // 每页大小
    val pageSize: Int,

    // 计划列表
    val plans: List<Plan>

)
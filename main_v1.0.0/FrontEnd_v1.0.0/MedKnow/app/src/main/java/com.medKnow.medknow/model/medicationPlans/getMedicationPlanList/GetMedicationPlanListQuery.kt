package com.medKnow.medknow.model.medicationPlans.getMedicationPlanList

import com.medKnow.medknow.model.medicationPlans.PlanStatus

// 获取用药计划列表（请求体）
data class GetMedicationPlanListQuery (

    // 筛选条件。不传则全部
    val status: PlanStatus?,

    // 页码
    val page: Int?,

    // 每页条数
    val size: Int?

)
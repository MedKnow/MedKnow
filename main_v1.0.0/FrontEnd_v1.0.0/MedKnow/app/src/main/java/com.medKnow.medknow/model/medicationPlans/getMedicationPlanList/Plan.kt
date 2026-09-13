package com.medKnow.medknow.model.medicationPlans.getMedicationPlanList

import com.medKnow.medknow.model.medicationPlans.PlanStatus

// 计划
data class Plan (

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: PlanStatus,

    // 药品数量
    val drugCount: Int,

    // 开始日期
    val startDate: String,

    // 结束日期
    val endDate: String,

    // 依从率。百分比
    val adherenceRate: Int,

    // 备注。可能为 null
    val notes: String?,

    // 主要药品。如“阿莫西林等 2 种”
    val mainDrugName: String,

    // 提醒方式。略缩展示
    val reminderMethods: List<String>

)
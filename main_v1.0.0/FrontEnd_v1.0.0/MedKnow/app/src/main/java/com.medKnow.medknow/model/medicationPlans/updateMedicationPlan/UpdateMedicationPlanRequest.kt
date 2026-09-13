package com.medKnow.medknow.model.medicationPlans.updateMedicationPlan

import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanReq

// 更新用药计划（请求体）
data class UpdateMedicationPlanRequest (

    // 药品清单
    val drugs: List<DrugCreateMedPlanReq>,

    // 开始日期
    val startDate: String,

    // 结束日期
    val endDate: String,

    // 备注
    val notes: String?,

    // 诊断/医嘱
    val diagnosis: String?,

    // 提醒方式
    val reminderMethods: List<String>

)
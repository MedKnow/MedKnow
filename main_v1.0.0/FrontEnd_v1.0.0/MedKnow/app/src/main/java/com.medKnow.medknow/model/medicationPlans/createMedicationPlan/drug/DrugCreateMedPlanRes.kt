package com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug

// 药品。创建用药计划返回响应
data class DrugCreateMedPlanRes (

    // 药品ID。计划内药品唯一标识
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 是否匹配药品库。false 表示药品名未匹配标准库
    val verified: Boolean,

    // 剂量风险标记。true 表示超出说明书剂量
    val dosageRisk: Boolean
)
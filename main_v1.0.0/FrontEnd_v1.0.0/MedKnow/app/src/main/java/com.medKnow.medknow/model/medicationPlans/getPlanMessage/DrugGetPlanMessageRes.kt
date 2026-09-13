package com.medKnow.medknow.model.medicationPlans.getPlanMessage

// 药品。获取计划详情
data class DrugGetPlanMessageRes (

    // 药品ID
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 剂量
    val dosage: String,

    // 频次
    val frequency: String,

    // 服药时间。可能多个逗号分隔
    val takeTime: String,

    // 服用方式
    val takeMethod: String,

    // 忌口
    val dietaryRestrictions: String?,

    // 验证标识
    val verified: Boolean,

    // 风险标识
    val dosageRisk: Boolean

)
package com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug

// 药品。创建用药计划请求体
data class DrugCreateMedPlanReq (

    // 药品名称。后端需额外拦截 SQL 关键字，匹配模式：^[\u4e00-\u9fa5a-zA-Z0-9·\-()]+$-
    val drugName: String,

    // 单次剂量。后端校验不超过说明书最大剂量，匹配模式：^[1-9]\d*(\.\d+)?(mg|g|ml|片|粒|支)$
    val dosage: String,

    // 服用频次。如“每日 3 次”
    val frequency: String,

    // 服药时间。06:30~23:00，多个用逗号分隔，匹配模式：^(0[6-9]|1\d|2[0-3]):[0-5]\d(,(0[6-9]|1\d|2[0-3]):[0-5]\d)*$
    val takeTime: String,

    // 服用方式。枚举值：BEFORE_MEAL 饭前 / AFTER_MEAL 饭后 / EMPTY_STOMACH 空腹 / BEFORE_SLEEP 睡前
    val takeMethod: TakeMethod,

    // 忌口提示。例如“忌酒”
    val dietaryRestrictions: String?

)
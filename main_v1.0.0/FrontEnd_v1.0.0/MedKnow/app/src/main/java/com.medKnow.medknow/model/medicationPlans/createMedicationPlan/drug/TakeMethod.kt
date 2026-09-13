package com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug

// 服用方式
enum class TakeMethod {

    // 饭前
    BEFORE_MEAL,

    // 饭后
    AFTER_MEAL,

    // 空腹
    EMPTY_STOMACH,

    // 睡前
    BEFORE_SLEEP,

    // 空值
    EMPTY

}
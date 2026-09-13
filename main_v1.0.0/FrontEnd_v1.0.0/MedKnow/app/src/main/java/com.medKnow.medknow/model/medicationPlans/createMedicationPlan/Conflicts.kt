package com.medKnow.medknow.model.medicationPlans.createMedicationPlan

//智能解析冲突
data class Conflicts (

    // 冲突药品。涉及的药品名称
    val drugs: List<String>,

    // 冲突类型。INTERVAL 间隔冲突 / CONTRAINDICATION 禁忌冲突
    val type: Type,

    // 冲突描述。详细提示
    val message: String

)
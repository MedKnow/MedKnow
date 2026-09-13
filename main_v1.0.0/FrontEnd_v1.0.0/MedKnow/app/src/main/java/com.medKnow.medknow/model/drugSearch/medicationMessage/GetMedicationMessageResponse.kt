package com.medKnow.medknow.model.drugSearch.medicationMessage

// 药品详情
data class GetMedicationMessageResponse (

    // 药品ID
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 通用名
    val genericName: String,

    // 药品类别。如“抗生素”
    val category: String?,

    // 适应症
    val indications: String?,

    // 用法用量
    val dosage: String?,

    // 不良反应
    val sideEffects: String?,

    // 禁忌
    val contraindications: String?,

    // 注意事项
    val precautions: String?,

    // 存储条件
    val storage: String?,

    // 收藏
    val isCollected: Boolean

)
package com.medKnow.medknow.model.drugSearch.medicationSearch

// 搜索结果
data class Results (

    // 药品ID
    val drugId: Int,

    // 药品名称。如“阿莫西林胶囊”
    val drugName: String,

    // 通用名。如“阿莫西林”
    val genericName: String,

    // 药品类别。如“抗生素”
    val category: String?,

    // 功效摘要。简要描述
    val summary: String?

)
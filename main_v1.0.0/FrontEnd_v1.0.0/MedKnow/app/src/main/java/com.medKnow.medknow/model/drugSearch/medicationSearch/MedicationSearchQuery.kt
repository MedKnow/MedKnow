package com.medKnow.medknow.model.drugSearch.medicationSearch

// 药品模糊搜索（请求体）
data class MedicationSearchQuery (

    // 药品名称。支持中英文模糊匹配，匹配模式：^[\u4e00-\u9fa5a-zA-Z0-9·\-()]+$
    val keyword: String,

    // 页码
    var page: Int? = 1,

    // 每页条数
    var size: Int? = 20,

)
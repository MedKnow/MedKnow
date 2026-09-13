package com.medKnow.medknow.model.drugSearch.medicationSearch

// 药品模糊搜索（返回响应）
data class MedicationSearchResponse (

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 搜索结果列表
    val results: List<Results>

)
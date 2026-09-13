package com.medKnow.medknow.model.visitNavigation.searchHospital

// 搜索医院（请求体）
data class SearchHospitalQuery (

    // 搜索关键词。医院名称或地址
    val keyword: String,

    // 页码
    val page: Int?,

    // 每页条数
    val size: Int?,

)
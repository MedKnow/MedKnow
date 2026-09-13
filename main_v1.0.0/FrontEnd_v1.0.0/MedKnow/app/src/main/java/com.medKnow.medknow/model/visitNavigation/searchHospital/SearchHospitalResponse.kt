package com.medKnow.medknow.model.visitNavigation.searchHospital

// 搜索医院（返回响应）
data class SearchHospitalResponse (

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 医院列表
    val list: List<HospitalSearch>

)
package com.medKnow.medknow.model.visitNavigation.recommendedHospitalList

// 推荐医院列表（返回响应）
data class RecommendedHospitalListResponse (

    // 总记录数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页条数
    val size: Int,

    // 推荐医院列表
    val list: List<Hospital>

)
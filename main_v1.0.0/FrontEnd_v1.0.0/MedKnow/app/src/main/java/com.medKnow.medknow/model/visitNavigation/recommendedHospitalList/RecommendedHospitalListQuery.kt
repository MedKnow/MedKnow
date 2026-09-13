package com.medKnow.medknow.model.visitNavigation.recommendedHospitalList

// 推荐医院列表（请求体）
data class RecommendedHospitalListQuery (

    // 经度。用户当前定位经度
    val longitude: Double,

    // 纬度。用户当前定位纬度
    val latitude: Double,

    // 筛选科室。如“内科”、“急诊科”，不传则综合推荐
    val department: String?,

    // 搜索半径（公里）。超出范围的不返回
    val distance: Int?,

    // 页码
    val page: Int?,

    // 每页条数。最大 20 避免接口过重
    val size: Int?

)
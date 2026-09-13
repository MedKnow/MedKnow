package com.medKnow.medknow.model.visitNavigation

// 医院详情
data class HospitalInformationResponse (

    // 医院ID
    val hospitalId: Int,

    // 医院名称
    val name: String,

    // 地址
    val address: String,

    // 联系电话
    val phone: String?,

    // 综合评分
    val rating: Double,

    // 医院简介
    val introduction: String?,

    // 科室列表
    val departments: List<Department>

)
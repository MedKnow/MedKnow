package com.medKnow.medknow.model.visitNavigation.searchHospital

// 医院
data class HospitalSearch (

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

    // 主要科室
    val mainDepartments: List<String>

)
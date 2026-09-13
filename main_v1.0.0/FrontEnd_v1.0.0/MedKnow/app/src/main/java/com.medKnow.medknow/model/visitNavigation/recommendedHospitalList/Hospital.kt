package com.medKnow.medknow.model.visitNavigation.recommendedHospitalList

// 推荐医院
data class Hospital (

    // 医院ID
    val hospitalId: Int,

    // 医院名称。全称
    val name: String,

    // 地址
    val address: String,

    // 距离（公里）。保留一位小数，如 3.2
    val distance: Double,

    // 综合评分。保留一位小数
    val rating: Double,

    // 强项科室。科室名称列表
    val mainDepartments: List<String>,

    // 预计到达时间（分钟）。驾车预计耗时
    val estimatedTime: Int,

    // 联系电话。可能为空
    val phone: String?

)
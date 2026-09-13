package com.medKnow.medknow.data.repository.fakeRepository

// 药品说明书信息
data class DrugSpecification (

    // 药品ID
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 单次最大剂量
    val maxSingleDose: Double,

    // 每日最大剂量
    val maxDailyDose: Double,

    // 标准单位
    val unit: String

)
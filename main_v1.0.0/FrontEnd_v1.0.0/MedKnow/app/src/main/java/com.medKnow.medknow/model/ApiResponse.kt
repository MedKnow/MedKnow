package com.medKnow.medknow.model

data class ApiResponse<T> (
    // 状态码
    val code: Int,

    // 状态描述
    val message: String,

    // 数据体
    val data: T?
)
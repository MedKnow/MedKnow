package com.medKnow.medknow.model.userModel.LoginByPhone

// 手机号验证码登录（返回响应）

data class LoginByPhoneResponse (

    // 令牌。用户登录令牌
    val token: String,

    // 用户ID。用户唯一标识
    val userId: Int,

    // 用户名。用户登录名称
    val userName: String,

    // 状态。用户当前状态
    val status: String

)
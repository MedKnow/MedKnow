package com.medKnow.medknow.model.userModel.LoginByPhone

// 手机号验证码登录（请求体）

data class LoginByPhoneRequest (

    // 手机号码。用户的手机号码
    val phone: String,

    // 验证码。短信验证码
    val code: String

)
package com.medKnow.medknow.model.userModel.sendCodeByPhone

// 发送短信验证码（请求体）
data class SendCodeByPhoneRequest (

    // 手机号码。用户的手机号码
    val phone: String

)
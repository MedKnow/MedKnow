package com.medKnow.medknow.model.userModel.sendCodeByPhone

// 发送短信验证码（返回响应）

data class SendCodeByPhoneResponse (

    // 过期秒数。数据的过期时间，单位秒
    val expireSeconds: Int

)
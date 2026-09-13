package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.userModel.LoginByPhone.LoginByPhoneResponse
import com.medKnow.medknow.model.userModel.sendCodeByPhone.SendCodeByPhoneResponse

// 认证模块
interface AuthRepository {

    // 接口1：发送短信验证码
    suspend fun sendCodeByPhone(phone: String): Result<SendCodeByPhoneResponse>

    // 接口2：手机号验证码登录
    suspend fun loginByPhone(phone: String, code: String): Result<LoginByPhoneResponse>

}
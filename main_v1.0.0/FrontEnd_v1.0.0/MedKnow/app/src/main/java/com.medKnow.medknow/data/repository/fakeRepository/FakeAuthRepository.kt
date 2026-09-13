package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.AuthRepository
import com.medKnow.medknow.model.userModel.LoginByPhone.LoginByPhoneResponse
import com.medKnow.medknow.model.userModel.sendCodeByPhone.SendCodeByPhoneResponse
import kotlinx.coroutines.delay

// 全局错误标志
object FakeErrorAuthFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}

// 假_认证模块
class FakeAuthRepository : AuthRepository {

    // 测试时覆盖 loginByPhone 的返回结果
    var loginByPhoneResult: Result<LoginByPhoneResponse>? = null

    // 发送短信验证码
    override suspend fun sendCodeByPhone(phone: String): Result<SendCodeByPhoneResponse> {
        delay(400)

        // 502 短信网关异常
        if (FakeErrorAuthFlags.isNetworkError) {
            return Result.failure(Exception("短信发送失败，请稍后再试"))
        }

        return when {

            // 400 手机号格式错误
            phone.length != 11 -> Result.failure(Exception("手机号格式不正确"))

            // 400 发送频率限制
            phone == "13800000000" -> Result.failure(Exception("发送过于频繁，请 60 秒后再试"))

            // 200 成功
            else -> Result.success(SendCodeByPhoneResponse(expireSeconds = 60))
        }

    }

    // 手机号验证码登录
    override suspend fun loginByPhone(phone: String, code: String): Result<LoginByPhoneResponse> {
        delay(600)

        // 优先使用测试显式返回值
        loginByPhoneResult?.let {
            return it
        }

        return if (phone == "13800138000" && code == "123456") {

            // 200 成功
            Result.success (
                LoginByPhoneResponse(
                    token = "fake_jwt+token_${System.currentTimeMillis()}",
                    userId = 1001,
                    userName = "周易权",
                    status = "NORMAL"
                )
            )

        } else if (code != "123456") {

            // 400 验证码错误或过期
            Result.failure(Exception("验证码错误"))

        } else {

            // 400 手机号不存在
            Result.failure(Exception("手机号未注册"))

        }
    }

}
package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.UserRepository
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.GetUserMessageResponse
import com.medKnow.medknow.model.userModel.GetUserPreferSettingResponse
import com.medKnow.medknow.model.userModel.ThemeColor
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarRequest
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarResponse
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageResponse
import kotlinx.coroutines.delay

// 全局错误标志
object FakeErrorUserFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

    // 虚假用户名是否超出规定长度
    var isFakeUserNameBeyondLength: Boolean = false

    // 虚假头像文件图片是否超出规定大小
    var isFakeUserAvatarBeyondSize: Boolean = false

}

// 假_用户模块
class FakeUserRepository : UserRepository {

    // 获取个人信息
    override suspend fun getUserMessage(): Result<GetUserMessageResponse> {
        delay(400L)

        // 401 Token 无效或未登录
        if (FakeErrorUserFlags.isUnauthorized) {
            return Result.failure(Exception("Token 已过期，请重新登录"))
        }

        // 500 服务器内部错误
        if (FakeErrorUserFlags.simulateServerError) {
            return Result.failure(Exception("服务器内部错误"))
        }

        // 200 成功
        return Result.success (
            GetUserMessageResponse(
                userId = "1001",
                userName = "周易权",
                phone = "13800000000",
                avatar = "http://localhost:8090/default.png",
                age = 30,
                gender = Gender.MALE,
                occupation = "清洁工",
                allergies = "花粉过敏",
                chronicDiseases = "糖尿病",
                status = "NORMAL",
                createdAt = "2026-08-07 12:13:50"
            )
        )
    }

    // 更新个人信息
    override suspend fun updateUserMessage(request: UpdateUserMessageRequest): Result<UpdateUserMessageResponse> {
        delay(600)

        // 400 参数校验失败
        if (FakeErrorUserFlags.isFakeUserNameBeyondLength) {
            return Result.failure(Exception("参数校验失败"))
        }

        // 401 Token 无效
        if (FakeErrorUserFlags.isUnauthorized) {
            return Result.failure(Exception("Token 已过期，请重新登录"))
        }

        // 200 成功
        return Result.success (
            UpdateUserMessageResponse(
                userName = "周易权",
                age = 45,
                gender = Gender.FEMALE,
                occupation = "网约车司机",
                allergies = "花粉过敏",
                chronicDiseases = "鼻炎"
            )
        )

    }

    // 获取用户偏好设置
    override suspend fun getUserPreferSetting(): Result<GetUserPreferSettingResponse> {
        delay(500)

        // 401 Token 无效或未登录
        if (FakeErrorUserFlags.isUnauthorized) {
            return Result.failure(Exception("Token 已过期，请重新登录"))
        }

        // 500 服务端异常
        if (FakeErrorUserFlags.simulateServerError) {
            return Result.failure(Exception("服务器繁忙，请稍后重试"))
        }

        // 200 成功
        return Result.success (
            GetUserPreferSettingResponse(
                themeColor = ThemeColor.GREEN,
                avatar = "http://localhost:8090/default.png",
                notificationEnabled = true,
                smsReminderEnabled = true
            )
        )

    }

    // 上传头像
    override suspend fun putAvatar(request: PutAvatarRequest): Result<PutAvatarResponse> {
        delay(600)

        // 400 文件校验失败
        if (FakeErrorUserFlags.isFakeUserAvatarBeyondSize) {
            return Result.failure(Exception("图片大小不能超过 2MB"))
        }

        // 401 未认证
        if (FakeErrorUserFlags.isUnauthorized) {
            return Result.failure(Exception("请先登录"))
        }

        // 500 服务器内部错误
        if (FakeErrorUserFlags.simulateServerError) {
            return Result.failure(Exception("系统繁忙，请稍后再试"))
        }

        // 200 上传成功
        return Result.success (
            PutAvatarResponse(
                avatarUrl = "http://localhost:8090/default.png"
            )
        )

    }

}
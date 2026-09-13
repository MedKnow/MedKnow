package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.userModel.GetUserMessageResponse
import com.medKnow.medknow.model.userModel.GetUserPreferSettingResponse
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarRequest
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarResponse
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageResponse

// 用户模块
interface UserRepository {

    // 接口3：获取个人信息
    suspend fun getUserMessage(): Result<GetUserMessageResponse>

    // 接口4：更新个人信息
    suspend fun updateUserMessage(request: UpdateUserMessageRequest): Result<UpdateUserMessageResponse>

    // 接口5：获取用户偏好设置
    suspend fun getUserPreferSetting(): Result<GetUserPreferSettingResponse>

    // 接口6：上传头像
    suspend fun putAvatar(request: PutAvatarRequest): Result<PutAvatarResponse>

}
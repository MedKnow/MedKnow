package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.ThemeColor

// 手机号验证码登录（返回响应）
data class LoginByPhoneResponseDto(

    // 令牌。用户登录令牌
    val token: String,

    // 用户ID。用户唯一标识
    val userId: Int,

    // 用户名。用户登录名称
    val userName: String,

    // 状态。用户当前状态
    val status: String

)

// 发送短信验证码（返回响应）
data class SendCodeByPhoneResponseDto(

    // 过期秒数。数据的过期时间，单位秒
    val expireSeconds: Int

)

// 上传头像（返回响应）
data class PutAvatarDto(

    // 头像地址。可访问的完整路径，如https://cdn.example.com/avatar/user_1.jpg，匹配模式：^http://
    val avatarUrl: String

)

// 更新个人信息（返回响应）
data class UpdateUserMessageResponseDto(

    // 用户名。用户昵称，2-20 个字符
    val userName: String?,

    // 年龄。0-120
    val age: Int?,

    // 性别。MALE / FEMALE / OTHER
    val gender: Gender?,

    // 职业
    val occupation: String?,

    // 过敏史。多个过敏原用逗号分隔
    val allergies: String?,

    //慢性疾病。慢性病是，多个用逗号分隔
    val chronicDiseases: String?

)

// 获取个人信息
data class GetUserMessageResponseDto(

    // 用户ID。登录时返回的那个用户ID
    val userId: String,

    // 用户名
    val userName: String,

    // 手机号，脱敏显示（中间 4 位用 * 代替）
    val phone: String,

    // 头像URL
    val avatar: String,

    // 年龄
    val age: Int,

    // 性别。MALE 男 / FEMALE 女 / OTHER 其他
    val gender: Gender,

    // 职业
    val occupation: String,

    // 过敏史
    val allergies: String?,

    // 慢性疾病
    val chronicDiseases: String?,

    // 账户状态。NORMAL 正常 / PENDING 待完善 / RESTRICTED 受限
    val status: String,

    // 注册时间。格式yyyy-MM-dd HH:mm:ss
    val createdAt: String

)

// 获取用户偏好设置
data class GetUserPreferSettingResponseDto(

    // 主题色。PURPLE 默认紫 / GREEN 护眼绿 / BLUE 深邃蓝 / ORANGE 暖阳橙 / GRAY 极简灰
    val themeColor: ThemeColor,

    // 头像URL。如果没设置过返回默认头像地址
    val avatar: String,

    // App推送开关。true 开启 / false 关闭
    val notificationEnabled: Boolean,

    //短信提醒开关。true 开启 / false 关闭
    val smsReminderEnabled: Boolean

)
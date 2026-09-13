package com.medKnow.medknow.model.userModel

// 获取用户偏好设置
data class GetUserPreferSettingResponse (

    // 主题色。PURPLE 默认紫 / GREEN 护眼绿 / BLUE 深邃蓝 / ORANGE 暖阳橙 / GRAY 极简灰
    val themeColor: ThemeColor,

    // 头像URL。如果没设置过返回默认头像地址
    val avatar: String,

    // App推送开关。true 开启 / false 关闭
    val notificationEnabled: Boolean,

    //短信提醒开关。true 开启 / false 关闭
    val smsReminderEnabled: Boolean

)
package com.medKnow.medknow.model.userModel.putAvatar

// 上传头像（返回响应）
data class PutAvatarResponse (

    // 头像地址。可访问的完整路径，如https://cdn.example.com/avatar/user_1.jpg，匹配模式：^http://
    val avatarUrl: String

)
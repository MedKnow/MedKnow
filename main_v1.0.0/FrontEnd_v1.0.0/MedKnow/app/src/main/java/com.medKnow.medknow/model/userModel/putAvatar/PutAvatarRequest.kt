package com.medKnow.medknow.model.userModel.putAvatar

import java.io.File

// 上传头像（请求体）
data class PutAvatarRequest (

    // 头像文件。图片文件，格式限jpg、jpeg、png，大小<= 2 MB
    val file: File

)
package com.medKnow.medknow.model.common.userFeedback

// 用户反馈（请求体）
data class UserFeedbackRequest (

    // 反馈内容。用户输入的建议或问题描述
    val content: String,

    // 联系方式。选填，用于回复用户，匹配模式：^1[3-9]\d{9}$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
    val contact: String?,

    //截图链接。选填，需先上传图片后传入URL
    val images: List<String>?

)
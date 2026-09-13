package com.medKnow.medknow.data.remote.dto

// 健康检查 data
data class HealthCheckData(

    // 服务状态。固定为 ok （正常），如果服务异常，这个接口根本不会返回 200
    val status: String,

    // 当前服务器时间。格式yyyy-MM-dd HH:mm:ss，用于前端与服务器时间同步，匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$
    val timestamp: String

)

// 用户反馈 dto
data class UserFeedbackDto(

    // 反馈内容。用户输入的建议或问题描述
    val content: String,

    // 联系方式。选填，用于回复用户，匹配模式：^1[3-9]\d{9}$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
    val contact: String? = null,

    //截图链接。选填，需先上传图片后传入URL
    val images: List<String>? = null

)
package com.medKnow.medknow.model.common

// 健康检查
data class HealthCheckResponse (

    // 服务状态。固定为 ok （正常），如果服务异常，这个接口根本不会返回 200
    val status: String,

    // 当前服务器时间。格式yyyy-MM-dd HH:mm:ss，用于前端与服务器时间同步，匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$
    val timestamp: String

)
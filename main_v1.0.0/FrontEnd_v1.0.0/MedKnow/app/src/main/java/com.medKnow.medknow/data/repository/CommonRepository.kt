package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.common.HealthCheckResponse
import com.medKnow.medknow.model.common.userFeedback.UserFeedbackRequest

// 通用模块
interface CommonRepository {

    // 接口29：健康检查
    suspend fun healthCheck(): Result<HealthCheckResponse>

    // 接口30：用户反馈
    suspend fun userFeedback(request: UserFeedbackRequest): Result<Unit>

}
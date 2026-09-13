package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.CommonRepository
import com.medKnow.medknow.model.common.HealthCheckResponse
import com.medKnow.medknow.model.common.userFeedback.UserFeedbackRequest
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 全局错误标志
object FakeErrorCommonFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}
// 假_通用模块
class FakeCommonRepository : CommonRepository {

    // 全局正则表达式
    val contactRegex = Regex("^1[3-9]\\d{9}\$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")

    // 日期时间格式化器
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // 健康检查
    override suspend fun healthCheck(): Result<HealthCheckResponse> {
        delay(500)

        if (FakeErrorCommonFlags.simulateServerError) {

            // 500 服务异常
            return Result.success (
                HealthCheckResponse(
                    status = "error",
                    timestamp = dateTimeFormatter.format(Date())
                )
            )

        } else {

            // 200 成功
            return Result.success (
                HealthCheckResponse(
                    status = "ok",
                    timestamp = dateTimeFormatter.format(Date())
                )
            )

        }
    }

    // 用户反馈
    override suspend fun userFeedback(request: UserFeedbackRequest): Result<Unit> {
        delay(400)

        return when {

            // 500 服务器内部错误
            FakeErrorCommonFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 401 未认证
            FakeErrorCommonFlags.isUnauthorized -> Result.failure(Exception("请先登录"))

            // 400 参数校验失败
            // 40009 反馈内容为空
            request.content.isBlank() -> Result.failure(Exception("反馈内容不能为空"))

            // 40009 反馈内容超长
            request.content.length > 500 -> Result.failure(Exception("反馈内容超出篇幅限制"))

            // 40010 截图数量超限
            (request.images?.size ?: 0) > 3 -> Result.failure(Exception("最多上传3张截图"))

            // 200 提交成功
            else -> Result.success(Unit)
        }
    }

}
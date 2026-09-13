package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.CheckinRepository
import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.checkins.CheckinsRequest
import com.medKnow.medknow.model.checkins.GetTodayCheckinsListResponse
import com.medKnow.medknow.model.checkins.Reminders
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsQuery
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsResponse
import com.medKnow.medknow.model.checkins.getComplianceStatistics.Period

import kotlinx.coroutines.delay

// 全局错误标志
object FakeErrorCheckinFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}
// 假_服药打卡模块
class FakeCheckinRepository : CheckinRepository {

    // 全局初始 reminderId
    private val primaryReminderId = 100

    // 虚假的提醒记录
    val reminders = mutableListOf(
        Reminders(
            reminderId = 101,
            planId = 1003,
            drugName = "布洛芬",
            scheduledTime = "2026-08-10 15:44",
            status = ReminderStatus.PENDING
        ),
        Reminders(
            reminderId = 102,
            planId = 1004,
            drugName = "蛇胆口服液",
            scheduledTime = "2026-08-15 17:30",
            status = ReminderStatus.TAKEN
        )
    )

    // 服药打卡
    override suspend fun checkins(request: CheckinsRequest): Result<Unit> {
        delay(300)

        val legalReminder = reminders.find { it.reminderId == request.reminderId }

        return when {

            // 500 服务器内部错误
            FakeErrorCheckinFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 提醒记录不存在
            legalReminder == null -> Result.failure(Exception("提醒记录不存在"))

            // 409 重复打卡
            legalReminder.status == ReminderStatus.TAKEN -> Result.failure(Exception("该提醒已打卡"))

            // 400 参数校验失败
            request.reminderId == null -> Result.failure(Exception("参数错误，reminderId 不能为空"))

            // 200 成功
            else -> {
                legalReminder.status = ReminderStatus.TAKEN
                Result.success(Unit)
            }
        }
    }

    // 获取今日待打卡列表
    override suspend fun getTodayCheckinsList(): Result<GetTodayCheckinsListResponse> {
        delay(500)

        val todayReminderList = reminders.map { it.status != ReminderStatus.TAKEN }

        return when {

            // 500 服务器内部错误
            FakeErrorCheckinFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 401 未认证
            FakeErrorCheckinFlags.isUnauthorized -> Result.failure(Exception("请先登录"))

            // 200 成功
            else -> {
                if (todayReminderList != null) {

                    // 有提醒数据
                    Result.success(
                        GetTodayCheckinsListResponse(
                            reminders = reminders.map { reminder ->
                                Reminders(
                                    reminderId = reminder.reminderId,
                                    planId = reminder.planId,
                                    drugName = reminder.drugName,
                                    scheduledTime = reminder.scheduledTime,
                                    status = reminder.status
                                )
                            }
                        )
                    )

                } else {

                    // 无提醒数据
                    Result.success(
                        GetTodayCheckinsListResponse(
                            reminders = emptyList()
                        )
                    )

                }
            }
        }
    }

    // 获取依从率统计
    override suspend fun getComplianceStatistics(query: GetComplianceStatisticsQuery): Result<GetComplianceStatisticsResponse> {
        delay(600)

        return when {

            // 500 服务器内部错误
            FakeErrorCheckinFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 401 未认证
            FakeErrorCheckinFlags.isUnauthorized -> Result.failure(Exception("请先登录"))

            // 400 参数无效
            query.period != Period.WEEK && query.period != Period.MONTH -> Result.failure(Exception("无效的统计周期，仅支持 WEEK/MONTH"))

            // 200 获取成功
            else -> Result.success(
                GetComplianceStatisticsResponse(
                    period = query.period,
                    totalDoses = 0,
                    confirmedDoses = 0,
                    missedDoses = 0,
                    adherenceRate = 0.0
                )
            )
        }
    }

}
package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.checkins.CheckinsRequest
import com.medKnow.medknow.model.checkins.GetTodayCheckinsListResponse
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsQuery
import com.medKnow.medknow.model.checkins.getComplianceStatistics.GetComplianceStatisticsResponse

// 服药打卡模块
interface CheckinRepository {

    // 接口14：服药打卡
    suspend fun checkins(request: CheckinsRequest): Result<Unit>

    // 接口15：获取今日待打卡列表
    suspend fun getTodayCheckinsList(): Result<GetTodayCheckinsListResponse>

    // 接口16：获取依从率统计
    suspend fun getComplianceStatistics(query: GetComplianceStatisticsQuery): Result<GetComplianceStatisticsResponse>

}
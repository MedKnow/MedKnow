package com.medKnow.medknow.model.checkins.getComplianceStatistics

// 获取依从率统计（请求体）
data class GetComplianceStatisticsQuery (

    // 统计周期
    val period: Period = Period.WEEK

)
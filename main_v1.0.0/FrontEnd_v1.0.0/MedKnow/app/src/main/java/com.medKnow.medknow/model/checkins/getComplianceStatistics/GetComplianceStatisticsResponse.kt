package com.medKnow.medknow.model.checkins.getComplianceStatistics

// 获取依从率统计（返回响应）
data class GetComplianceStatisticsResponse (

    // 统计周期
    val period: Period,

    // 计划总次数。该周期内应服药总次数
    val totalDoses: Int,

    // 已确认次数。用户实际打卡次数
    val confirmedDoses: Int,

    // 漏服次数。超时未处理的提醒数
    val missedDoses: Int,

    // 依从率。百分比，保留一位小数
    val adherenceRate: Double

)
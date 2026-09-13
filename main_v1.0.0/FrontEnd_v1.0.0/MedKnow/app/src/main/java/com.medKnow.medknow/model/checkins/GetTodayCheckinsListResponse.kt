package com.medKnow.medknow.model.checkins

// 获取今日待打卡列表
data class GetTodayCheckinsListResponse (

    // 今日提醒列表
    val reminders: List<Reminders>

)
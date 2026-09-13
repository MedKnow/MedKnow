package com.medKnow.medknow.model.checkins

// 服药打卡
data class CheckinsRequest (

    // 提醒ID。要打卡的提醒记录ID
    val reminderId: Int,

    // 实际服药时间。用户真实服药的时间点，匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$
    val actualTime: String,

    // 是否延迟打卡。超过提醒时间 30 分钟后打卡为 true
    val isLate: Boolean

)
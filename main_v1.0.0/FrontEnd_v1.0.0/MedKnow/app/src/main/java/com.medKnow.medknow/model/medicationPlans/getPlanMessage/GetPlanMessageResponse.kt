package com.medKnow.medknow.model.medicationPlans.getPlanMessage

import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.Conflicts

// 获取计划详情
data class GetPlanMessageResponse (

    // 计划ID
    val planId: Int,

    // 计划状态
    var status: PlanStatus,

    // 开始日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    val startDate: String,

    // 结束日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    val endDate: String,

    // 备注
    val notes: String?,

    // 诊断
    val diagnosis: String?,

    // 依从率
    val adherenceRate: Int,

    // 提醒方式
    val reminderMethods: List<String>,

    // 药品清单。详细约束信息
    val drugs: List<DrugGetPlanMessageRes>,

    // 冲突列表。结构同创建接口
    val conflicts: List<Conflicts>,

    // 今日提醒。当天所有提醒
    val todayReminders: List<TodayReminders>

)
package com.medKnow.medknow.model.medicationPlans.createMedicationPlan

import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.ReminderMethod
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanRes

// 创建用药计划（返回响应）
data class CreateMedicationPlanResponse (

    // 计划ID。新创建的唯一标识
    val planId: Int,

    // 计划状态。初始为草稿
    var status: PlanStatus,

    // 开始日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    var startDate: String,

    // 结束日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    var endDate: String,

    // 备注
    var notes: String?,

    // 诊断
    var diagnosis: String?,

    // 提醒方式。用户选择的提醒渠道
    var reminderMethods: List<String> = listOf("ALARM"),

    // 药品列表。返回服务端校验结果
    var drugs: List<DrugCreateMedPlanRes>,

    // 智能解析冲突
    var conflicts: List<Conflicts>

    ) {
    val reminderMethod: List<ReminderMethod>
        get() = reminderMethods.map { raw ->
            try {
                ReminderMethod.valueOf(raw.uppercase())
            } catch (e: IllegalArgumentException) {
                ReminderMethod.UNKNOWN
            }
        }
}
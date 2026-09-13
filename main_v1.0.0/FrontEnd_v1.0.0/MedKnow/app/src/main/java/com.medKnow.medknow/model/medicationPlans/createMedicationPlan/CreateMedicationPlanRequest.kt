package com.medKnow.medknow.model.medicationPlans.createMedicationPlan

import com.medKnow.medknow.model.medicationPlans.ReminderMethod
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanReq

// 创建用药计划（请求体）
data class CreateMedicationPlanRequest (

    // 药品列表
    val drugs: List<DrugCreateMedPlanReq>,

    // 开始日期。yyyy-MM--dd，>=当天
    val startDate: String,

    // 结束日期。yyyy-MM-dd，> startDate
    val endDate: String,

    // 备注
    val notes: String?,

    // 诊断/医嘱。用户输入的诊断信息
    val diagnosis: String?,

    // 提醒方式。至少选一种
    val reminderMethods: List<String> = listOf("ALARM")

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
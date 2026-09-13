package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.medicationPlans.ActiveMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.ResumeMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.StopMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.Conflicts
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.Type
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanRes
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListResponse
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.Plan
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.DrugGetPlanMessageRes
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.TodayReminders

// 智能解析冲突
data class ConflictsDto(

    // 冲突药品。涉及的药品名称
    val drugs: List<String> = emptyList(),

    // 冲突类型。INTERVAL 间隔冲突 / CONTRAINDICATION 禁忌冲突
    val type: String,

    // 冲突描述。详细提示
    val message: String

) {
    fun toDomain(): Conflicts {
        return Conflicts(
            drugs = drugs,
            type = runCatching { Type.valueOf(type) }.getOrDefault(Type.INTERVAL),
            message = message
        )
    }
}

// 创建用药计划响应药品
data class DrugCreateMedPlanResDto(

    // 药品ID。计划内药品唯一标识
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 是否匹配药品库。false 表示药品名未匹配标准库
    val verified: Boolean,

    // 剂量风险标记。true 表示超出说明书剂量
    val dosageRisk: Boolean

) {
    fun toDomain(): DrugCreateMedPlanRes {
        return DrugCreateMedPlanRes(
            drugId = drugId,
            drugName = drugName,
            verified = verified,
            dosageRisk = dosageRisk
        )
    }
}

// 创建用药计划 data
data class CreateMedicationPlanData(

    // 计划ID。新创建的唯一标识
    val planId: Int,

    // 计划状态。初始为草稿
    var status: String = "DRAFT",

    // 开始日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    var startDate: String,

    // 结束日期。匹配模式：^\d{4}-\d{2}-\d{2}$
    var endDate: String,

    // 备注
    var notes: String? = null,

    // 诊断
    var diagnosis: String? = null,

    // 提醒方式。用户选择的提醒渠道
    var reminderMethods: List<String> = listOf("ALARM"),

    // 药品列表。返回服务端校验结果
    var drugs: List<DrugCreateMedPlanResDto> = emptyList(),

    // 智能解析冲突
    var conflicts: List<ConflictsDto> = emptyList()

) {
    fun toDomain(): CreateMedicationPlanResponse {
        return CreateMedicationPlanResponse(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            startDate = startDate,
            endDate = endDate,
            notes = notes,
            diagnosis = diagnosis,
            reminderMethods = reminderMethods,
            drugs = drugs.map { it.toDomain() },
            conflicts = conflicts.map { it.toDomain() }
        )
    }
}

// 计划
data class PlanDto(

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: String,

    // 药品数量
    val drugCount: Int,

    // 开始日期
    val startDate: String,

    // 结束日期
    val endDate: String,

    // 依从率。百分比
    val adherenceRate: Int,

    // 备注。可能为 null
    val notes: String? = null,

    // 主要药品。如“阿莫西林等 2 种”
    val mainDrugName: String,

    // 提醒方式。略缩展示
    val reminderMethods: List<String> = emptyList()

) {
    fun toDomain(): Plan {
        return Plan(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            drugCount = drugCount,
            startDate = startDate,
            endDate = endDate,
            adherenceRate = adherenceRate,
            notes = notes,
            mainDrugName = mainDrugName,
            reminderMethods = reminderMethods
        )
    }
}

// 获取用药计划列表 data
data class GetMedicationPlanListData(

    // 总记录数
    val total: Int,

    // 当前页码
    val pageNum: Int,

    // 每页大小
    val pageSize: Int,

    // 计划列表
    val plans: List<PlanDto> = emptyList()

) {
    fun toDomain(): GetMedicationPlanListResponse {
        return GetMedicationPlanListResponse(
            total = total,
            pageNum = pageNum,
            pageSize = pageSize,
            plans = plans.map { it.toDomain() }
        )
    }
}

// 获取计划详情药品
data class DrugGetPlanMessageResDto(

    // 药品ID
    val drugId: Int,

    // 药品名称
    val drugName: String,

    // 剂量
    val dosage: String,

    // 频次
    val frequency: String,

    // 服药时间。可能多个逗号分隔
    val takeTime: String,

    // 服用方式
    val takeMethod: String,

    // 忌口
    val dietaryRestrictions: String? = null,

    // 验证标识
    val verified: Boolean,

    // 风险标识
    val dosageRisk: Boolean

) {
    fun toDomain(): DrugGetPlanMessageRes {
        return DrugGetPlanMessageRes(
            drugId = drugId,
            drugName = drugName,
            dosage = dosage,
            frequency = frequency,
            takeTime = takeTime,
            takeMethod = takeMethod,
            dietaryRestrictions = dietaryRestrictions,
            verified = verified,
            dosageRisk = dosageRisk
        )
    }
}

// 今日提醒。当天所有提醒
data class TodayRemindersDto(

    // 提醒ID
    val reminderId: Int,

    // 药品名
    val drugName: String,

    // 计划提醒时间。匹配模式：^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$
    val scheduledTime: String,

    // 提醒状态
    var status: String,

) {
    fun toDomain(): TodayReminders {
        return TodayReminders(
            reminderId = reminderId,
            drugName = drugName,
            scheduledTime = scheduledTime,
            status = runCatching { ReminderStatus.valueOf(status) }.getOrDefault(ReminderStatus.PENDING)
        )
    }
}

// 获取计划详情 data
data class GetPlanMessageData(

    // 计划ID
    val planId: Int,

    // 计划状态
    var status: String,

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
    val drugs: List<DrugGetPlanMessageResDto>,

    // 冲突列表。结构同创建接口
    val conflicts: List<ConflictsDto>,

    // 今日提醒。当天所有提醒
    val todayReminders: List<TodayRemindersDto>

) {
    fun toDomain(): GetPlanMessageResponse {
        return GetPlanMessageResponse(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            startDate = startDate,
            endDate = endDate,
            notes = notes,
            diagnosis = diagnosis,
            adherenceRate = adherenceRate,
            reminderMethods = reminderMethods,
            drugs = drugs.map { it.toDomain() },
            conflicts = conflicts.map { it.toDomain() },
            todayReminders = todayReminders.map { it.toDomain() }
        )
    }
}

// 激活用药计划 data
data class ActiveMedicationPlanData(

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: String,

    // 激活时间。格式yyyy-MM--ddTHH:mm:ssZ
    val activatedAt: String

) {
    fun toDomain(): ActiveMedicationPlanResponse {
        return ActiveMedicationPlanResponse(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            activatedAt = activatedAt
        )
    }
}

// 暂停用药计划 data
data class StopMedicationPlanData (

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: String,

    // 暂停时间
    val pausedAt: String

) {
    fun toDomain(): StopMedicationPlanResponse {
        return StopMedicationPlanResponse(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            pausedAt = pausedAt
        )
    }
}

// 恢复用药计划 data
data class ResumeMedicationPlanData(

    // 计划ID
    val planId: Int,

    // 计划状态
    val status: String,

    // 恢复时间
    val resumedAt: String

) {
    fun toDomain(): ResumeMedicationPlanResponse {
        return ResumeMedicationPlanResponse(
            planId = planId,
            status = runCatching { PlanStatus.valueOf(status) }.getOrDefault(PlanStatus.DRAFT),
            resumedAt = resumedAt
        )
    }
}
package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.MedicationPlanRepository
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanRequest
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanRes
import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.medicationPlans.ActiveMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.ResumeMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.StopMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListQuery
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListResponse
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.Plan
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.DrugGetPlanMessageRes
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.TodayReminders
import com.medKnow.medknow.model.medicationPlans.updateMedicationPlan.UpdateMedicationPlanRequest

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 全局错误标志
object FakeErrorMedicationPlanFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

    // 虚假用户名是否超出规定长度
    var isFakeUserNameBeyondLength: Boolean = false

    // 虚假头像文件图片是否超出规定大小
    var isFakeUserAvatarBeyondSize: Boolean = false

}

// 假_用药计划模块
class FakeMedicationPlanRepository : MedicationPlanRepository {

    // 全局正则表达式
    // 药品名称正则表达式
    private val drugNameRegex = Regex("^[\\u4e00-\\u9fa5a-zA-Z0-9·\\-()]+\$")
    // 单次剂量正则表达式
    private val dosageRegex = Regex("^([1-9]\\d*(\\.\\d+)?)(mg|g|ml|片|粒|支)\$")
    // 服药时间正则表达式
    private val takeTimeRegex = Regex("^(0[6-9]|1\\d|2[0-3]):[0-5]\\d(,(0[6-9]|1\\d|2[0-3]):[0-5]\\d)*\$")

    // 全局初始 planId
    private var primaryPlanId: Int = 1000
    // 全局初始 DrugId
    private var primaryDrugId: Int = 10000
    // 全局初始 reminderId
    private var primaryReminderId: Int = 100000

    // 日期格式化器
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    // 日期时间格式化器
    private val dateTimeFormatter = SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ", Locale.getDefault())

    // 虚假的药品说明书
    private val drugSpec = mutableListOf(
        DrugSpecification(
            drugId = 100_1,
            drugName = "阿莫西林",
            maxSingleDose = 2.0,
            maxDailyDose = 6.0,
            unit = "粒"
        ),
        DrugSpecification(
            drugId = 100_2,
            drugName = "莫匹罗星",
            maxSingleDose = 5.0,
            maxDailyDose = 15.0,
            unit = "mg"
        )
    )

    // 虚假的用药计划列表（已创建）
    private val plans = mutableListOf(
        GetPlanMessageResponse(
            planId = 1001,
            status = PlanStatus.DRAFT,
            startDate = "2026-08-07",
            endDate = "2026-08-10",
            notes = "这是一条假数据",
            diagnosis = "医生说没救了",
            adherenceRate = 0,
            reminderMethods = listOf("ALARM"),
            drugs = listOf (
                DrugGetPlanMessageRes(
                    drugId = 100_2,
                    drugName = "莫匹罗星",
                    dosage = "10 mg",
                    frequency = "每日 3 次",
                    takeTime = "07:00,12:00,18:00",
                    takeMethod = "AFTER_MEAL",
                    dietaryRestrictions = "忌酒",
                    verified = true,
                    dosageRisk = false
                )
            ),
            conflicts = emptyList(),
            todayReminders = listOf (
                TodayReminders(
                    reminderId = 1000001,
                    drugName = "莫匹罗星",
                    scheduledTime = "2026-07-14 07:00",
                    status = ReminderStatus.TRIGGERED
                )
            )
        ),
        GetPlanMessageResponse(
            planId = 1002,
            status = PlanStatus.ACTIVE,
            startDate = "2026-08-10",
            endDate = "2026-08-20",
            notes = "这是一条真数据",
            diagnosis = "医生还有的救",
            adherenceRate = 1,
            reminderMethods = listOf("ALARM", "SMS"),
            drugs = listOf (
                DrugGetPlanMessageRes(
                    drugId = 100_1,
                    drugName = "阿莫西林",
                    dosage = "1 片",
                    frequency = "每日 3 次",
                    takeTime = "08:00,17:00,22:00",
                    takeMethod = "BEFORE_MEAL",
                    dietaryRestrictions = "忌辛辣",
                    verified = true,
                    dosageRisk = false
                )
            ),
            conflicts = emptyList(),
            todayReminders = listOf (
                TodayReminders(
                    reminderId = 1000002,
                    drugName = "阿莫西林",
                    scheduledTime = "2026-07-14 08:00",
                    status = ReminderStatus.TAKEN
                )
            )
        )
    )

    // 将 drugs.dosage 分成用量与单位
    private fun parseDosage(dosage: String): Pair<Double, String> {
        val match = dosageRegex.find(dosage) ?: return Pair(0.0, "")
        val dosageValue = match.groupValues[1].toDouble()
        val dosageUnit = match.groupValues[3]
        return Pair(dosageValue, dosageUnit)
    }

    // 创建用药计划
    override suspend fun createMedicationPlan(request: CreateMedicationPlanRequest): Result<CreateMedicationPlanResponse> {
        delay(500)

        // 检验 drugs 列表 drugName 是否全都合法，并提取出非法的 drugName
        val illegalDrugNames = request.drugs
            .map { it.drugName }
            .filter { !it.matches(drugNameRegex) }

        // 校验 drugs 列表 takeTime 是否全都合法，并提取出非法的 takeTime
        val illegalTakeTimes = request.drugs
            .map { it.takeTime }
            .filter { !it.matches(takeTimeRegex) }

        // 校验 drugs 列表 dosage 是否全都合法，并提取出非法的 dosage
        val illegalDosages = request.drugs
            .map { it.dosage }
            .filter { !it.matches(dosageRegex) }

        // 通用字段校验
        // drugs.drugName 格式校验
        if(illegalDrugNames.isNotEmpty()) {
            return Result.failure(Exception("以下药品名称格式不正确：${illegalDrugNames.joinToString("、")}"))
        }
        // drugs.takeTime 格式校验
        if (illegalTakeTimes.isNotEmpty()) {
            return Result.failure(Exception("服药时间格式不正确"))
        }

        // drugs.dosage 格式校验
        if (illegalDosages.isNotEmpty()) {
            return Result.failure(Exception("单次剂量格式不正确"))
        }

        // drugs.dosage 单位校验
        for (drug in request.drugs) {
            val (values, units) = parseDosage(drug.dosage)
            val requestDrugSpec = drugSpec
                .find { it.drugName == drug.drugName }
            if (requestDrugSpec == null) continue
            if (requestDrugSpec.unit != units) {
                return Result.failure(Exception("${drug.drugName}的单次剂量单位应为${requestDrugSpec.unit}，当前为${units}"))
            }
            // 40003 剂量超限
            if (values > requestDrugSpec.maxSingleDose) {
                return Result.failure(Exception("${drug.drugName}单次剂量超过说明书规定的最大剂量"))
            }
        }

        // 获取今日日期（不含时分秒）
        val todayDate = dateFormatter.parse(dateFormatter.format(Date()))!!

        // 开始日期与结束日期格式校验
        if (request.startDate != null && request.endDate != null) {

            val startDate = try {
                dateFormatter.parse(request.startDate)!!
            } catch (e: Exception) {
                return Result.failure(Exception("开始日期格式错误"))
            }
            val endDate = try {
                dateFormatter.parse(request.endDate)!!
            } catch (e: Exception) {
                return Result.failure(Exception("结束日期格式错误"))
            }

            // 40002 日期错误
            if(startDate.before(todayDate)) {
                return Result.failure(Exception("开始日期不得早于今日"))
            }

            if(endDate.before(startDate)) {
                return Result.failure(Exception("结束日期必须晚于开始日期"))
            }

        } else {
            return Result.failure(Exception("日期不能为空"))
        }

        val drugsResult = request.drugs.map { drug ->
            DrugCreateMedPlanRes(
                drugId = ++primaryPlanId,
                drugName = drug.drugName,
                verified = false,
                dosageRisk = false
            )
        }

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 403 权限不足
            FakeErrorMedicationPlanFlags.isUnauthorized -> Result.failure(Exception("请先完成注册或完善信息"))

            // 400 请求有误

            // 40001 通用校验
            request.drugs.isNullOrEmpty() -> Result.failure(Exception("药品名称不能为空"))

            // 201 已创建
            else -> Result.success (
                CreateMedicationPlanResponse (
                    planId = ++primaryPlanId,
                    status = PlanStatus.ACTIVE,
                    startDate = request.startDate,
                    endDate = request.endDate,
                    notes = request.notes,
                    diagnosis = request.diagnosis,
                    reminderMethods = request.reminderMethods,
                    drugs = drugsResult,
                    conflicts = emptyList()
                )
            )
        }
    }

    // 获取用药计划列表
    override suspend fun getMedicationPlanList(query: GetMedicationPlanListQuery): Result<GetMedicationPlanListResponse> {
        delay(400)

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 403 未登录
            FakeErrorMedicationPlanFlags.isUnauthorized -> Result.failure(Exception("请先登录"))

            // 400 无效状态筛选值
            query.status !in PlanStatus.entries.toSet() -> Result.failure(Exception("无效的状态筛选值"))

            // 200 成功
            else -> {
                if (query.status != null) {

                    // 输入包含筛选条件
                    val legalPlans = plans.filter { it.status == query.status }

                    if (legalPlans.isNotEmpty()) {

                        // 存在符合筛选条件的用药计划（有数据）
                        val legalPlansList = legalPlans.map { legalPlan ->
                            Plan(
                                planId = legalPlan.planId,
                                status = legalPlan.status,
                                drugCount = legalPlan.drugs.size,
                                startDate = legalPlan.startDate,
                                endDate = legalPlan.endDate,
                                adherenceRate = 0,
                                notes = legalPlan.notes,
                                mainDrugName = "${legalPlan.drugs.first().drugName}等${legalPlan.drugs.size}种",
                                reminderMethods = legalPlan.reminderMethods,
                            )
                        }
                        Result.success(
                            GetMedicationPlanListResponse(
                                total = legalPlans.size,
                                pageSize = query.size ?: 10,
                                pageNum = (query.size ?: 10) / legalPlans.size,
                                plans = legalPlansList
                            )
                        )

                    } else {

                        // 无符合筛选条件的用药计划（无数据）
                        Result.success(
                            GetMedicationPlanListResponse(
                                total = 0,
                                pageNum = 1,
                                pageSize = 10,
                                plans = emptyList()
                            )
                        )

                    }
                } else {

                    // 输入不包含筛选条件（有数据）
                    val plansList = plans.map { plan ->
                        Plan(
                            planId = plan.planId,
                            status = plan.status,
                            drugCount = plan.drugs.size,
                            startDate = plan.startDate,
                            endDate = plan.endDate,
                            adherenceRate = 0,
                            notes = plan.notes,
                            mainDrugName = "${plan.drugs.first().drugName}等${plan.drugs.size}种",
                            reminderMethods = plan.reminderMethods,
                        )
                    }
                    Result.success(
                        GetMedicationPlanListResponse(
                            total = plans.size,
                            pageSize = query.size ?: 10,
                            pageNum = (query.size ?: 10) / plans.size,
                            plans = plansList
                        )
                    )
                }
            }
        }
    }

    // 获取计划详情
    override suspend fun getPlanMessage(planId: Int): Result<GetPlanMessageResponse> {
        delay(600)

        // 500 服务器内部错误
        if (FakeErrorMedicationPlanFlags.simulateServerError) {
            return Result.failure(Exception("系统繁忙，请稍后再试"))
        }

        // 403 无权访问
        if (FakeErrorMedicationPlanFlags.isUnauthorized) {
            return Result.failure(Exception("无权访问该计划"))
        }

        // 404 计划不存在
        val legalPlan = plans.find { it.planId == planId }
        if (legalPlan == null) {
            return Result.failure(Exception("用药计划不存在"))
        }

        // 200 成功
        val getMedicationPlanResponseLegalDrug = legalPlan.drugs.map { drug ->
            DrugGetPlanMessageRes(
                drugId = drug.drugId,
                drugName = drug.drugName,
                dosage = "0",
                frequency = "每日 3 次",
                takeTime = "00:00",
                takeMethod = "冲泡",
                dietaryRestrictions = "无",
                verified = drug.verified,
                dosageRisk = drug.dosageRisk
            )
        }

        val getMedicationPlanResponseTodayReminder = legalPlan.drugs.map { drug ->
            TodayReminders(
                reminderId = ++primaryReminderId,
                drugName = drug.drugName,
                scheduledTime = "无",
                status = ReminderStatus.TAKEN
            )
        }

        return Result.success (
            GetPlanMessageResponse(
                planId = legalPlan.planId,
                status = legalPlan.status,
                startDate = legalPlan.startDate,
                endDate = legalPlan.endDate,
                notes = legalPlan.notes,
                diagnosis = legalPlan.diagnosis,
                adherenceRate = 0,
                reminderMethods = legalPlan.reminderMethods,
                drugs = getMedicationPlanResponseLegalDrug,
                conflicts = emptyList(),
                todayReminders = getMedicationPlanResponseTodayReminder
            )
        )
    }

    // 更新用药计划
    override suspend fun updateMedicationPlan(planId: Int, request: UpdateMedicationPlanRequest): Result<Unit> {
        delay(300)

        val legalPlan = plans.find { it.planId == planId}
        val legalPlanIndex = plans.indexOfFirst {it.planId == planId}

        // 检验 drugs 列表 drugName 是否全都合法，并提取出非法的 drugName
        val illegalDrugNames = request.drugs
            .map { it.drugName }
            .filter { !it.matches(drugNameRegex) }

        // 校验 drugs 列表 takeTime 是否全都合法，并提取出非法的 takeTime
        val illegalTakeTimes = request.drugs
            .map { it.takeTime }
            .filter { !it.matches(takeTimeRegex) }

        // 校验 drugs 列表 dosage 是否全都合法，并提取出非法的 dosage
        val illegalDosages = request.drugs
            .map { it.dosage }
            .filter { !it.matches(dosageRegex) }

        // 通用字段校验
        // drugs.drugName 格式校验
        if(illegalDrugNames.isNotEmpty()) {
            return Result.failure(Exception("以下药品名称格式不正确：${illegalDrugNames.joinToString("、")}"))
        }
        // drugs.takeTime 格式校验
        if (illegalTakeTimes.isNotEmpty()) {
            return Result.failure(Exception("服药时间格式不正确"))
        }

        // drugs.dosage 格式校验
        if (illegalDosages.isNotEmpty()) {
            return Result.failure(Exception("单次剂量格式不正确"))
        }

        // drugs.dosage 单位校验
        for (drug in request.drugs) {
            val (values, units) = parseDosage(drug.dosage)
            val requestDrugSpec = drugSpec
                .find { it.drugName == drug.drugName }
            if (requestDrugSpec == null) continue
            if (requestDrugSpec.unit != units) {
                return Result.failure(Exception("${drug.drugName}的单次剂量单位应为${requestDrugSpec.unit}，当前为${units}"))
            }
            // 40003 剂量超限
            if (values > requestDrugSpec.maxSingleDose) {
                return Result.failure(Exception("${drug.drugName}单次剂量超过说明书规定的最大剂量"))
            }
        }

        // 获取今日日期（不含时分秒）
        val todayDate = dateFormatter.parse(dateFormatter.format(Date()))!!

        // 开始日期与结束日期格式校验
        if (request.startDate != null && request.endDate != null) {

            val startDate = try {
                dateFormatter.parse(request.startDate)!!
            } catch (e: Exception) {
                return Result.failure(Exception("开始日期格式错误"))
            }
            val endDate = try {
                dateFormatter.parse(request.endDate)!!
            } catch (e: Exception) {
                return Result.failure(Exception("结束日期格式错误"))
            }

            // 40002 日期错误
            if(startDate.before(todayDate)) {
                return Result.failure(Exception("开始日期不得早于今日"))
            }

            if(endDate.before(startDate)) {
                return Result.failure(Exception("结束日期必须晚于开始日期"))
            }

        } else {
            return Result.failure(Exception("日期不能为空"))
        }

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 计划不存在
            legalPlan == null -> Result.failure(Exception("用药计划不存在"))

            // 409 状态冲突：非草稿/暂停不可更新
            legalPlan.status != PlanStatus.DRAFT && legalPlan.status != PlanStatus.PAUSED -> Result.failure(Exception("仅草稿或暂停状态的计划可更新"))

            // 400 参数校验失败
            legalPlan.drugs == null -> Result.failure(Exception("药品名称不能为空"))

            // 200 更新成功
            else -> {
                plans[legalPlanIndex] = legalPlan.copy(
                    planId = legalPlan.planId,
                    status = legalPlan.status,
                    startDate = request.startDate,
                    endDate = request.endDate,
                    notes = request.notes,
                    diagnosis = request.diagnosis,
                    adherenceRate = 0,
                    reminderMethods = request.reminderMethods,
                    drugs = request.drugs.map { drug ->
                        DrugGetPlanMessageRes(
                            drugId = ++primaryDrugId,
                            drugName = drug.drugName,
                            dosage = drug.dosage,
                            frequency = drug.frequency,
                            takeTime = drug.takeTime,
                            takeMethod = "${drug.takeMethod}",
                            dietaryRestrictions = drug.dietaryRestrictions,
                            verified = false,
                            dosageRisk = false
                        )
                    },
                    conflicts = emptyList(),
                    todayReminders = request.drugs.map { drug ->
                        TodayReminders(
                            reminderId = ++primaryReminderId,
                            drugName = drug.drugName,
                            scheduledTime = drug.takeTime,
                            status = ReminderStatus.TAKEN
                        )
                    }
                )
                Result.success(Unit)
            }
        }
    }

    // 激活用药计划
    override suspend fun activeMedicationPlan(planId: Int): Result<ActiveMedicationPlanResponse> {
        delay(400)

        val legalPlan = plans.find { it.planId == planId }
        val legalPlanIndex = plans.indexOfFirst { it.planId == planId }

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 计划不存在
            legalPlan == null -> Result.failure(Exception("用药计划不存在"))

            // 409 状态冲突：当前状态不可激活
            legalPlan.status == PlanStatus.ACTIVE -> Result.failure(Exception("当前状态不可激活"))

            // 400 提醒方式无效
            legalPlan.reminderMethods == null -> Result.failure(Exception("请至少选择一种提醒方式"))

            // 200 成功
            else -> {

                plans[legalPlanIndex] = legalPlan.copy(status = PlanStatus.ACTIVE)

                Result.success(
                    ActiveMedicationPlanResponse(
                        planId = planId,
                        status = PlanStatus.ACTIVE,
                        activatedAt = dateTimeFormatter.format(Date())
                    )
                )

            }
        }
    }

    // 暂停用药计划
    override suspend fun stopMedicationPlan(planId: Int): Result<StopMedicationPlanResponse> {
        delay(400)

        val legalPlan = plans.find { it.planId == planId }
        val legalPlanIndex = plans.indexOfFirst { it.planId == planId }

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 计划不存在
            legalPlan == null -> Result.failure(Exception("用药计划不存在"))

            // 409 状态冲突：仅生效中可暂停
            legalPlan.status != PlanStatus.ACTIVE -> Result.failure(Exception("仅生效中的计划可以暂停"))

            // 200 暂停成功
            else -> {

                plans[legalPlanIndex] = legalPlan.copy(status = PlanStatus.PAUSED)

                Result.success(
                    StopMedicationPlanResponse(
                        planId = legalPlan.planId,
                        status = PlanStatus.PAUSED,
                        pausedAt = dateTimeFormatter.format(Date())
                    )
                )

            }
        }
    }

    // 恢复用药计划
    override suspend fun resumeMedicationPlan(planId: Int): Result<ResumeMedicationPlanResponse> {
        delay(400)

        val legalPlan = plans.find { it.planId == planId }
        val legalPlanIndex = plans.indexOfFirst { it.planId == planId }

        return when {

            // 500 服务器内部错误
            FakeErrorMedicationPlanFlags.simulateServerError -> Result.failure(Exception("系统繁忙，请稍后再试"))

            // 404 计划不存在
            legalPlan == null -> Result.failure(Exception("用药计划不存在"))

            // 409 状态冲突：仅暂停中可恢复
            legalPlan.status != PlanStatus.PAUSED -> Result.failure(Exception("仅暂停中的计划可以恢复"))

            // 200 恢复成功
            else -> {

                plans[legalPlanIndex] = legalPlan.copy(status = PlanStatus.ACTIVE)

                Result.success(
                    ResumeMedicationPlanResponse(
                        planId = legalPlan.planId,
                        status = PlanStatus.ACTIVE,
                        resumedAt = dateTimeFormatter.format(Date())
                    )
                )

            }
        }
    }

}
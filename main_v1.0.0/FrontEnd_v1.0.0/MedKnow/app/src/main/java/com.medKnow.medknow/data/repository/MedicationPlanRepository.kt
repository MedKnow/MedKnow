package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.medicationPlans.ActiveMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.ResumeMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.StopMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanRequest
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanResponse
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListQuery
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListResponse
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.updateMedicationPlan.UpdateMedicationPlanRequest

// 用药计划模块
interface MedicationPlanRepository {

    // 接口7：创建用药计划
    suspend fun createMedicationPlan(request: CreateMedicationPlanRequest): Result<CreateMedicationPlanResponse>

    // 接口8：获取用药计划列表
    suspend fun getMedicationPlanList(query: GetMedicationPlanListQuery): Result<GetMedicationPlanListResponse>

    // 接口9：获取计划详情
    suspend fun getPlanMessage(planId: Int): Result<GetPlanMessageResponse>

    // 接口10：更新用药计划
    suspend fun updateMedicationPlan(planId: Int, request: UpdateMedicationPlanRequest): Result<Unit>

    // 接口11：激活用药计划
    suspend fun activeMedicationPlan(planId: Int): Result<ActiveMedicationPlanResponse>

    // 接口12：暂停用药计划
    suspend fun stopMedicationPlan(planId: Int): Result<StopMedicationPlanResponse>

    // 接口13：恢复用药计划
    suspend fun resumeMedicationPlan(planId: Int): Result<ResumeMedicationPlanResponse>

}
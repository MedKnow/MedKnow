package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeMedicationPlanRepository
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.TakeMethod
import com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel.EMPTY_PLAN
import com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel.MedicationPlanViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

// 用药计划页 ViewModel 单元测试
@OptIn(ExperimentalCoroutinesApi::class)
class MedicationPlanViewModelTest {

    // 替换 Dispatcher.main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeMedicationPlanRepository = FakeMedicationPlanRepository()
    private fun createViewModel() = MedicationPlanViewModel(fakeMedicationPlanRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isPlanListLoading)
        assertFalse(state.isCreatePlanLoading)
        assertFalse(state.isAddDrugLoading)
        assertFalse(state.isPlanInformationLoading)
        assertFalse(state.isActivePlanLoading)
        assertFalse(state.isStopPlanLoading)
        assertFalse(state.isResumePlanLoading)
        assertFalse(state.isUpdatePlanLoading)
        assertFalse(state.isUpdateDrugLoading)
        assertTrue(state.hasMore)
        assertFalse(state.isLoadingMore)
        assertFalse(state.isSortLoading)
        assertFalse(state.isLoading)
        assertFalse(state.isSavingId)
        assertNull(state.loadingPlanListErrorMessage)
        assertNull(state.addDrugErrorMessage)
        assertNull(state.createPlanErrorMessage)
        assertNull(state.planInformationErrorMessage)
        assertNull(state.activePlanErrorMessage)
        assertNull(state.stopPlanErrorMessage)
        assertNull(state.resumePlanErrorMessage)
        assertNull(state.updatePlanErrorMessage)
        assertNull(state.updateDrugErrorMessage)
        assertNull(state.loadingMoreErrorMessage)
        assertTrue(state.medicationPlanList.isEmpty())
        assertTrue(state.drugList.isEmpty())
        assertTrue(state.updatePlanDrugs.isEmpty())
        assertEquals(0, state.totalPlanNumber)
        assertEquals(1, state.currentPage)
        assertEquals(3, state.pageSize)
        assertEquals(-1, state.complexOperationPlanId)
        assertEquals(EMPTY_PLAN, state.selectedPlan)
        assertEquals(
            listOf(
                PlanStatus.ACTIVE, PlanStatus.DRAFT, PlanStatus.PAUSED, PlanStatus.COMPLETED, PlanStatus.EXPIRED
            ),
            state.statuses
        )

    }

    // 自动加载计划列表
    @Test
    fun initLoadsPlanListAutomatically() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isPlanListLoading)
        assertFalse(state.isLoading)
        assertNull(state.loadingPlanListErrorMessage)
        assertEquals(2, state.medicationPlanList.size)
        assertEquals(2, state.totalPlanNumber)
    }

    // 创建计划所需药品
    @Test
    fun onAddDrugToCreateMedicationPlan() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        viewModel.onCreateDrug(
            drugName = "阿莫西林",
            dosage = "1 片",
            frequency = "每日 3 次",
            takeTime = "08:00",
            takeMethod = TakeMethod.BEFORE_MEAL,
            dietaryRestrictions = "忌辛辣"
        )

        assertFalse(state.isAddDrugLoading)
        assertNull(state.addDrugErrorMessage)
        assertEquals(1, state.drugList.size)
        assertEquals("阿莫西林", state.drugList.first().drugName)
        assertEquals(TakeMethod.BEFORE_MEAL, state.drugList.first().takeMethod)
    }

    // 未添加药品时创建计划，应提示错误
    @Test
    fun onCreateMedicationPlanWithEmptyDrugList() {
        val viewModel = createViewModel()

        viewModel.onCreateMedicationPlan(
            startDate = "2026-08-10",
            endDate = "2026-08-20",
            notes = null,
            diagnosis = null,
            reminderMethods = listOf("ALARM")
        )

        val state = viewModel.uiState.value
        assertFalse(state.isCreatePlanLoading)
        assertEquals("请至少添加一种药品", state.createPlanErrorMessage)
    }

    // 成功创建计划
    @Test
    fun onCreateMedicationPlanSuccess() {
        val viewModel = createViewModel()

        viewModel.onCreateDrug(
            drugName = "阿莫西林",
            dosage = "1 片",
            frequency = "每日 3 次",
            takeTime = "08:00",
            takeMethod = TakeMethod.BEFORE_MEAL,
            dietaryRestrictions = "忌辛辣"
        )

        viewModel.onCreateMedicationPlan(
            startDate = "2026-08-10",
            endDate = "2026-08-20",
            notes = null,
            diagnosis = null,
            reminderMethods = listOf("ALARM")
        )

        val state = viewModel.uiState.value
        assertFalse(state.isCreatePlanLoading)
        assertNull(state.createPlanErrorMessage)
        assertTrue(state.drugList.isEmpty())
    }

    // 获取计划详情
    @Test
    fun onMedicationPlanInformation() {
        val viewModel = createViewModel()
        val planId = 1001

        viewModel.onMedicationPlanInformation(planId)

        val state = viewModel.uiState.value
        assertFalse(state.isPlanInformationLoading)
        assertNull(state.planInformationErrorMessage)
        assertEquals(planId, state.selectedPlan.planId)
    }

    // 激活计划
    @Test
    fun onActiveMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onActivePlan(1001)

        val state = viewModel.uiState.value
        assertFalse(state.isActivePlanLoading)
        assertNull(state.activePlanErrorMessage)
        assertEquals(PlanStatus.ACTIVE, state.selectedPlan.status)
        assertEquals(1001, state.selectedPlan.planId)
    }

    // 激活非“草稿”计划
    @Test
    fun onActiveUnDraftMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onActivePlan(1002)

        val state = viewModel.uiState.value
        assertFalse(state.isActivePlanLoading)
        assertEquals("日程表当前状态无法激活", state.activePlanErrorMessage)
    }

    // 暂停计划
    @Test
    fun onStopMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onStopPlan(1002)

        val state = viewModel.uiState.value
        assertFalse(state.isStopPlanLoading)
        assertNull(state.stopPlanErrorMessage)
        assertEquals(PlanStatus.PAUSED, state.selectedPlan.status)
        assertEquals(1002, state.selectedPlan.planId)
    }

    // 暂停非“激活”计划
    @Test
    fun onStopUnActiveMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onStopPlan(1001)

        val state = viewModel.uiState.value
        assertFalse(state.isStopPlanLoading)
        assertEquals("日程表当前状态无法暂停", state.stopPlanErrorMessage)
    }

    // 恢复计划
    @Test
    fun onResumeMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onStopPlan(1002)
        viewModel.onResumePlan(1002)

        val state = viewModel.uiState.value
        assertFalse(state.isResumePlanLoading)
        assertNull(state.resumePlanErrorMessage)
        assertEquals(PlanStatus.ACTIVE, state.selectedPlan.status)
    }

    // 恢复非“暂停”计划
    @Test
    fun onResumeUnStopMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onResumePlan(1001)

        val state = viewModel.uiState.value
        assertFalse(state.isResumePlanLoading)
        assertEquals("日程表当前状态无法恢复", state.resumePlanErrorMessage)
    }

    // 更新计划所需药品
    @Test
    fun onAddDrugToMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onUpdatePlanDrug(
            drugName = "莫匹罗星",
            dosage = "10 mg",
            frequency = "每日 3 次",
            takeTime = "07:00",
            takeMethod = TakeMethod.AFTER_MEAL,
            dietaryRestrictions = "忌酒"
        )

        val state = viewModel.uiState.value
        assertFalse(state.isUpdateDrugLoading)
        assertNull(state.updateDrugErrorMessage)
        assertEquals(1, state.updatePlanDrugs.size)
        assertEquals("莫匹罗星", state.updatePlanDrugs.first().drugName)
    }

    // 更新用药计划
    @Test
    fun onUpdateMedicationPlan() {
        val viewModel = createViewModel()

        viewModel.onUpdatePlanDrug(
            drugName = "莫匹罗星",
            dosage = "10 mg",
            frequency = "每日 3 次",
            takeTime = "07:00",
            takeMethod = TakeMethod.AFTER_MEAL,
            dietaryRestrictions = "忌酒"
        )

        viewModel.onUpdateMedicationPlan(
            planId = 1001,
            startDate = "2026-08-07",
            endDate = "2026-08-12",
            notes = "更新",
            diagnosis = "更新诊断",
            reminderMethods = listOf("ALARM")
        )

        val state = viewModel.uiState.value
        assertFalse(state.isUpdatePlanLoading)
        assertNull(state.updatePlanErrorMessage)
        assertTrue(state.updatePlanDrugs.isEmpty())
        assertEquals(-1, state.complexOperationPlanId)
    }

    // 保存复杂操作计划 id
    @Test
    fun onSaveComplexOperationPlanId() {
        val viewModel = createViewModel()

        viewModel.onComplexOperationPlanIdSave(1001)

        val state = viewModel.uiState.value
        assertFalse(state.isSavingId)
        assertEquals(1001, state.complexOperationPlanId)
    }

    // 不保存非法 id
    @Test
    fun onIgnoreInValidPlanId() {
        val viewModel = createViewModel()

        viewModel.onComplexOperationPlanIdSave(-1)

        val state = viewModel.uiState.value
        assertFalse(state.isSavingId)
        assertEquals(-1, state.complexOperationPlanId)
    }
}
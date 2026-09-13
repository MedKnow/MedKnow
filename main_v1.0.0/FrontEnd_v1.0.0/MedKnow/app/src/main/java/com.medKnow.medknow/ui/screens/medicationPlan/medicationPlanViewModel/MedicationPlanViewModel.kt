package com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.MedicationPlanRepository
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.CreateMedicationPlanRequest
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanReq
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.TakeMethod
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.GetMedicationPlanListQuery
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.Plan
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.updateMedicationPlan.UpdateMedicationPlanRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

 val EMPTY_PLAN = GetPlanMessageResponse(
    planId = -1,
    status = PlanStatus.DRAFT,
    startDate = "",
    endDate = "",
    notes = null,
    diagnosis = null,
    adherenceRate = -1,
    reminderMethods = emptyList(),
    drugs = emptyList(),
    conflicts = emptyList(),
    todayReminders = emptyList()
)

// 日程表页 ViewModel
class MedicationPlanViewModel @Inject constructor(private val medicationPlanRepository: MedicationPlanRepository): ViewModel() {

    // 定义 MedicationPlanUiState 数据类
    data class MedicationPlanUiState(

        // 加载计划列表错误信息
        val loadingPlanListErrorMessage: String? = null,
        // 是否处于正在加载用药计划列表状态
        val isPlanListLoading: Boolean = false,
        // 加载后的用药计划列表
        val medicationPlanList: List<Plan> = emptyList(),
        // 用药计划列表总项数
        val totalPlanNumber: Int = 0,
        // 创建用药计划时所需药品列表
        val drugList: List<DrugCreateMedPlanReq> = emptyList(),
        // 是否处于创建计划状态
        val isCreatePlanLoading: Boolean = false,
        // 是否处于新增所需药品状态
        val isAddDrugLoading: Boolean = false,
        // 新增所需药品错误信息
        val addDrugErrorMessage: String? = null,
        // 创建计划错误信息
        val createPlanErrorMessage: String? = null,
        // 是否正在加载计划详情
        val isPlanInformationLoading: Boolean = false,
        // 加载计划详情错误信息
        val planInformationErrorMessage: String? = null,
        // 当前选择的计划
        val selectedPlan: GetPlanMessageResponse = EMPTY_PLAN,
        // 是否正在激活计划
        val isActivePlanLoading: Boolean = false,
        // 激活计划错误信息
        val activePlanErrorMessage: String? = null,
        // 是否正在停止计划
        val isStopPlanLoading: Boolean = false,
        // 停止计划错误信息
        val stopPlanErrorMessage: String? = null,
        // 是否正在恢复计划
        val isResumePlanLoading: Boolean = false,
        // 恢复计划错误信息
        val resumePlanErrorMessage: String? = null,
        // 更新用药计划所需药品列表
        val updatePlanDrugs: List<DrugCreateMedPlanReq> = emptyList(),
        // 是否处于正在更新状态
        val isUpdatePlanLoading: Boolean = false,
        // 更新计划错误信息
        val updatePlanErrorMessage: String? = null,
        // 是否处于正在更新药品状态
        val isUpdateDrugLoading: Boolean = false,
        // 更新药品错误信息
        val updateDrugErrorMessage: String? = null,
        // 当前页码
        val currentPage: Int = 1,
        // 是否还有更多
        val hasMore: Boolean = true,
        // 是否正在加载更多
        val isLoadingMore: Boolean = false,
        // 每页计划数量
        val pageSize: Int = 3,
        // 加载更多时错误信息
        val loadingMoreErrorMessage: String? = null,
        // 是否正在筛选
        val isSortLoading: Boolean = false,
        // 可供筛选的选项
        val statuses: List<PlanStatus> = listOf(
            PlanStatus.ACTIVE, PlanStatus.DRAFT, PlanStatus.PAUSED, PlanStatus.COMPLETED, PlanStatus.EXPIRED
        ),
        // 筛选时错误信息
        val sortedErrorMessage: String? = null,
        // 用户当前选择的筛选条件
        val selectedStatus: PlanStatus? = null,
        // 全局加载状态
        val isLoading: Boolean = false,
        // 将要执行复杂操作的计划 id
        val complexOperationPlanId: Int = -1,
        // 是否正在保存 id
        val isSavingId: Boolean = false,
        // 保存时错误信息
        val savingIdErrorMessage: String? = null

    )

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(MedicationPlanUiState())
    val uiState: StateFlow<MedicationPlanUiState> = _uiState.asStateFlow()

    // 自动加载当前计划列表
    init {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        onPlanListPreview()

        _uiState.update {
            it.copy(
                isLoading = false
            )
        }
    }

    // 获取当前所有用药计划概览
    fun onPlanListPreview() {

        // 防止重复请求
        if (_uiState.value.isPlanListLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isPlanListLoading = true,
                loadingPlanListErrorMessage = null,
                medicationPlanList = emptyList(),
                totalPlanNumber = 0,
                currentPage = 1,
                hasMore = true
            )
        }

        // 加载已创建用药计划列表
        viewModelScope.launch {
            try {
                val targetQuery = GetMedicationPlanListQuery(
                    status = null,
                    page = _uiState.value.currentPage,
                    size = _uiState.value.pageSize
                )

                val response = medicationPlanRepository.getMedicationPlanList(targetQuery).getOrThrow()

                _uiState.update {
                    it.copy(
                        isPlanListLoading = false,
                        loadingPlanListErrorMessage = null,
                        medicationPlanList = response.plans,
                        totalPlanNumber = response.total
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPlanListLoading = false,
                        loadingPlanListErrorMessage = e.message ?: "日程表加载失败",
                        medicationPlanList = emptyList(),
                        totalPlanNumber = 0
                    )
                }
            }
        }

    }

    // 新建用药计划
    fun onCreateMedicationPlan(
        startDate: String,
        endDate: String,
        notes: String?,
        diagnosis: String?,
        reminderMethods: List<String>
    ) {

        val currentPlanList = _uiState.value.medicationPlanList
        val currentDrugList = _uiState.value.drugList

        // 防止重复请求
        if (_uiState.value.isCreatePlanLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isCreatePlanLoading = true,
                createPlanErrorMessage = null
            )
        }

        // 校验药品列表
        if (_uiState.value.drugList.isEmpty()) {
            _uiState.update {
                it.copy(
                    createPlanErrorMessage = "请至少添加一种药品"
                )
            }
            return
        }

        // 创建用药计划
        viewModelScope.launch {
            try {

                val targetDrugs = _uiState.value.drugList

                val targetRequest = CreateMedicationPlanRequest(
                    drugs = targetDrugs,
                    diagnosis = diagnosis,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    reminderMethods = reminderMethods
                )

                val response = medicationPlanRepository.createMedicationPlan(targetRequest).getOrThrow()

                _uiState.update {
                    it.copy(
                        isCreatePlanLoading = false,
                        createPlanErrorMessage = null,
                        medicationPlanList = currentPlanList + Plan(
                            planId = response.planId,
                            startDate = response.startDate,
                            endDate = response.endDate,
                            drugCount = response.drugs.size,
                            mainDrugName = "${response.drugs.first().drugName}等共${response.drugs.size}种",
                            notes = response.notes,
                            reminderMethods = response.reminderMethods,
                            adherenceRate = 0,
                            status = PlanStatus.DRAFT
                        ),
                        drugList = emptyList()
                    )
                }
                onPlanListPreview()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatePlanLoading = false,
                        createPlanErrorMessage = e.message ?: "创建日程表失败",
                        medicationPlanList = currentPlanList,
                        drugList = currentDrugList
                    )
                }
            }
        }

    }

    // 新建用药计划所需药品
    fun onCreateDrug(
        drugName: String,
        dosage: String,
        frequency: String,
        takeTime: String,
        takeMethod: TakeMethod,
        dietaryRestrictions: String?
    ) {

        // 防止重复请求
        if (_uiState.value.isAddDrugLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isAddDrugLoading = true,
                addDrugErrorMessage = null
            )
        }

        // 新增药品
        viewModelScope.launch {

            val currentDrugList = _uiState.value.drugList
            try {
                val targetDrug = DrugCreateMedPlanReq(
                    drugName = drugName,
                    dosage = dosage,
                    frequency = frequency,
                    takeTime = takeTime,
                    takeMethod = takeMethod,
                    dietaryRestrictions = dietaryRestrictions
                )
                _uiState.update {
                    it.copy(
                        isAddDrugLoading = false,
                        addDrugErrorMessage = null,
                        drugList = it.drugList + targetDrug
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAddDrugLoading = false,
                        addDrugErrorMessage = e.message ?: "新增药品失败，请稍后再试",
                        drugList = currentDrugList
                    )
                }
            }

        }

    }

    // 获取用药计划详情
    fun onMedicationPlanInformation(planId: Int) {

        // 防止重复请求
        if (_uiState.value.isPlanInformationLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isPlanInformationLoading = true,
                planInformationErrorMessage = null,
                selectedPlan = EMPTY_PLAN
            )
        }

        // 查看用药计划详情
        viewModelScope.launch {

            try {
                val response = medicationPlanRepository.getPlanMessage(planId).getOrThrow()

                _uiState.update {
                    it.copy(
                        isPlanInformationLoading = false,
                        planInformationErrorMessage = null,
                        selectedPlan = response
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isPlanInformationLoading = false,
                        planInformationErrorMessage = e.message ?: "查看失败，请稍后再试",
                        selectedPlan = EMPTY_PLAN
                    )
                }
            }

        }

    }

    // 激活用药计划
    fun onActivePlan(planId: Int) {

        // 防止重复请求
        if (_uiState.value.isActivePlanLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isActivePlanLoading = true,
                activePlanErrorMessage = null,
            )
        }

        // 激活计划
        viewModelScope.launch {
            try {
                val targetPlan = medicationPlanRepository.getPlanMessage(planId).getOrThrow()

                _uiState.update {
                    it.copy(
                        selectedPlan = targetPlan
                    )
                }

                // 防止重复激活
                if (targetPlan.status != PlanStatus.DRAFT) {
                    _uiState.update {
                        it.copy(
                            isActivePlanLoading = false,
                            activePlanErrorMessage = "日程表当前状态无法激活",
                            selectedPlan = EMPTY_PLAN
                        )
                    }
                    return@launch
                } else {
                    medicationPlanRepository.activeMedicationPlan(planId).getOrThrow()
                    _uiState.update {
                        it.copy(
                            isActivePlanLoading = false,
                            activePlanErrorMessage = null,
                            selectedPlan = it.selectedPlan.copy(status = PlanStatus.ACTIVE)
                        )
                    }
                    onPlanListPreview()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isActivePlanLoading = false,
                        activePlanErrorMessage = e.message ?: "激活失败，请稍后重试"
                    )
                }

            }
        }

    }

    // 恢复用药计划
    fun onResumePlan(planId: Int) {

        // 防止重复请求
        if (_uiState.value.isResumePlanLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isResumePlanLoading = true,
                resumePlanErrorMessage = null
            )
        }

        // 恢复用药计划
        viewModelScope.launch {
            try {
                val targetPlan = medicationPlanRepository.getPlanMessage(planId).getOrThrow()

                _uiState.update {
                    it.copy(
                        selectedPlan = targetPlan
                    )
                }

                // 判断用药计划是否可恢复
                if (targetPlan.status != PlanStatus.PAUSED) {
                    _uiState.update {
                        it.copy(
                            isResumePlanLoading = false,
                            resumePlanErrorMessage = "日程表当前状态无法恢复",
                            selectedPlan = EMPTY_PLAN
                        )
                    }
                    return@launch
                } else {
                    medicationPlanRepository.resumeMedicationPlan(planId).getOrThrow()

                    _uiState.update {
                        it.copy(
                            isResumePlanLoading = false,
                            resumePlanErrorMessage = null,
                            selectedPlan = it.selectedPlan.copy(status = PlanStatus.ACTIVE)
                        )
                    }
                    onPlanListPreview()
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isResumePlanLoading = false,
                        resumePlanErrorMessage = e.message ?: "恢复计划失败"
                    )
                }

            }
        }

    }

    // 暂停用药计划
    fun onStopPlan(planId: Int) {

        // 防止重复请求
        if (_uiState.value.isStopPlanLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isStopPlanLoading = true,
                stopPlanErrorMessage = null
            )
        }

        // 暂停用药计划
        viewModelScope.launch {
            try {
                val targetPlan = medicationPlanRepository.getPlanMessage(planId).getOrThrow()

                _uiState.update {
                    it.copy(
                        selectedPlan = targetPlan
                    )
                }

                if(targetPlan.status != PlanStatus.ACTIVE) {
                    _uiState.update {
                        it.copy(
                            isStopPlanLoading = false,
                            stopPlanErrorMessage = "日程表当前状态无法暂停",
                            selectedPlan = EMPTY_PLAN
                        )
                    }
                    return@launch
                } else {
                    medicationPlanRepository.stopMedicationPlan(planId).getOrThrow()

                    _uiState.update {
                        it.copy(
                            isStopPlanLoading = false,
                            stopPlanErrorMessage = null,
                            selectedPlan = it.selectedPlan.copy(status = PlanStatus.PAUSED)
                        )
                    }
                    onPlanListPreview()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isStopPlanLoading = false,
                        stopPlanErrorMessage = e.message ?: "暂停失败，请稍后再试"
                    )
                }
            }
        }

    }

    // 更新用药计划所需药品
    fun onUpdatePlanDrug(
        drugName: String,
        dosage: String,
        frequency: String,
        takeTime: String,
        takeMethod: TakeMethod,
        dietaryRestrictions: String?
    ) {
        // 防止重复请求
        if (_uiState.value.isUpdateDrugLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isUpdateDrugLoading = true,
                updateDrugErrorMessage = null
            )
        }

        // 新增药品
        viewModelScope.launch {

            val currentDrugList = _uiState.value.updatePlanDrugs
            try {
                val targetDrug = DrugCreateMedPlanReq(
                    drugName = drugName,
                    dosage = dosage,
                    frequency = frequency,
                    takeTime = takeTime,
                    takeMethod = takeMethod,
                    dietaryRestrictions = dietaryRestrictions
                )
                _uiState.update {
                    it.copy(
                        isUpdateDrugLoading = false,
                        updateDrugErrorMessage = null,
                        updatePlanDrugs = it.updatePlanDrugs + targetDrug
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdateDrugLoading = false,
                        updateDrugErrorMessage = e.message ?: "新增药品失败，请稍后再试",
                        updatePlanDrugs = currentDrugList
                    )
                }
            }

        }

    }

    // 更新用药计划
    fun onUpdateMedicationPlan(
        planId: Int,
        startDate: String,
        endDate: String,
        notes: String?,
        diagnosis: String?,
        reminderMethods: List<String>
    ) {

        val currentDrugList = _uiState.value.updatePlanDrugs

        // 防止重复请求
        if (_uiState.value.isUpdatePlanLoading) {
            return
        }

        // 校验计划 id 合法性
        if (planId <= 0) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isUpdatePlanLoading = true,
                updatePlanErrorMessage = null
            )
        }

        // 更新用药计划
        viewModelScope.launch {
            try {

                val targetDrugs = _uiState.value.updatePlanDrugs

                val targetRequest = UpdateMedicationPlanRequest(
                    drugs = targetDrugs,
                    diagnosis = diagnosis,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes,
                    reminderMethods = reminderMethods
                )

                medicationPlanRepository.updateMedicationPlan(planId, targetRequest).getOrThrow()

                _uiState.update {
                    it.copy(
                        isUpdatePlanLoading = false,
                        updatePlanErrorMessage = null,
                        updatePlanDrugs = emptyList(),
                        complexOperationPlanId = -1
                    )
                }
                onPlanListPreview()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatePlanLoading = false,
                        updatePlanErrorMessage = e.message ?: "更新日程表失败",
                        updatePlanDrugs = currentDrugList
                    )
                }
            }
        }

    }

    // 加载更多计划
    fun loadMore() {
        val state = _uiState.value

        // 防止重复请求并判断是否有更多计划
        if (state.isLoadingMore || !state.hasMore) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isLoadingMore = true,
                loadingMoreErrorMessage = null,
                isLoading = true
            )
        }

        // 加载更多
        viewModelScope.launch {
            try {
                val nextPage = state.currentPage + 1

                val query = GetMedicationPlanListQuery(
                    status = state.selectedStatus,
                    page = nextPage,
                    size = state.pageSize
                )
                val response = medicationPlanRepository.getMedicationPlanList(query).getOrThrow()

                _uiState.update {
                    it.copy(
                        medicationPlanList = it.medicationPlanList + response.plans,
                        isLoadingMore = false,
                        isLoading = false,
                        loadingMoreErrorMessage = null,
                        currentPage = nextPage,
                        hasMore = it.medicationPlanList.size + response.plans.size < response.total
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        isLoading = false,
                        loadingMoreErrorMessage = e.message ?: "加载更多计划失败",
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        isLoading = false
                    )
                }
            }
        }

    }

    // 筛选当前所有计划
    fun onPlanSort(status: PlanStatus?) {

        // 防止重复请求
        if (_uiState.value.isSortLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isSortLoading = true,
                sortedErrorMessage = null,
                medicationPlanList = emptyList(),
                selectedStatus = status,
                currentPage = 1,
                hasMore = true
            )
        }

        // 展示筛选后的计划列表
        viewModelScope.launch {
            try {

                val query = GetMedicationPlanListQuery(
                    status = status,
                    page = 1,
                    size = _uiState.value.pageSize
                )

                val response = medicationPlanRepository.getMedicationPlanList(query).getOrThrow()

                _uiState.update {
                    it.copy(
                        isSortLoading = false,
                        medicationPlanList = response.plans,
                        totalPlanNumber = response.total,
                        hasMore = response.plans.size < response.total
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSortLoading = false,
                        sortedErrorMessage = e.message ?: "筛选失败"
                    )
                }
            }
        }

    }

    // 保存复杂操作对象计划 id
    fun onComplexOperationPlanIdSave(planId: Int) {

        // 计划 id 合法校验
        if (planId <= 0) {
            return
        }

        // 防止重复请求
        if (_uiState.value.isSavingId) {
            return
        }

        // 进入保存状态
        _uiState.update {
            it.copy(
                isSavingId = true,
                savingIdErrorMessage = null
            )
        }

        // 保存 id
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isSavingId = false,
                        complexOperationPlanId = planId
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSavingId = false,
                        savingIdErrorMessage = e.message ?: "查找计划失败，请稍后再试",
                        complexOperationPlanId = -1
                    )
                }
            }
        }

    }

    // 辅助函数

    // 防止重复导航
    fun onNavigatedToHome() {
        _uiState.update {
            it.copy(
                isLoading = false
            )
        }
    }

}
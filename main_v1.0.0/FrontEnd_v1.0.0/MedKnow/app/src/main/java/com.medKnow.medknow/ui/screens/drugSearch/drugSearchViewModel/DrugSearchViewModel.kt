package com.medKnow.medknow.ui.screens.drugSearch.drugSearchViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.DrugRepository
import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchQuery
import com.medKnow.medknow.model.drugSearch.medicationSearch.Results
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// GetMedicationMessage 空值
val EMPTY_MEDICATION = GetMedicationMessageResponse(
    drugId = 0,
    drugName = "",
    genericName = "",
    category = null,
    indications = null,
    dosage = null,
    sideEffects = null,
    contraindications = null,
    precautions = null,
    storage = null,
    isCollected = false
)

// 药品搜索页 ViewModel
class DrugSearchViewModel @Inject constructor(private val drugRepository: DrugRepository): ViewModel() {

    // 搜索页状态
    enum class SearchState {
        HISTORY,
        SUGGESTION
    }

    // 定义 DrugSearchUiState 数据类
    data class DrugSearchUiState(

        // 用户当前搜索关键词
        val searchKeyword: String = "",
        // 是否正在搜索
        val isSearching: Boolean = false,
        // 搜索时错误信息
        val searchingErrorMessage: String? = null,
        // 推荐列表
        val suggestions: List<Results> = emptyList(),
        // 当前搜索状态
        val searchState: SearchState = SearchState.HISTORY,
        // 用户当前选择的药品
        val selectedDrug: GetMedicationMessageResponse = EMPTY_MEDICATION,
        // 是否正在加载药品详情
        val isLoadingDrug: Boolean = false,
        // 加载详情时错误信息
        val loadingDrugErrorMessage: String? = null,
        // 是否正在收藏药品
        val isCollectDrug: Boolean = false,
        // 收藏药品时错误信息
        val collectDrugErrorMessage: String? = null

    )

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(DrugSearchUiState())
    val uiState: StateFlow<DrugSearchUiState> = _uiState.asStateFlow()

    // 防抖任务
    private var searchJob: Job? = null

    // 输入框内容变化
    fun onKeywordChange(keyword: String) {
        _uiState.update {
            it.copy(
                searchKeyword = keyword,
                searchingErrorMessage = null
            )
        }

        // 取消防抖任务
        searchJob?.cancel()

        if (keyword.isEmpty()) {
            // 无输入，展示历史搜索
            _uiState.update {
                it.copy(
                    searchState = SearchState.HISTORY,
                    suggestions = emptyList(),
                    isSearching = false
                )
            }
        } else {
            // 有输入，展示推荐结果
            _uiState.update {
                it.copy(
                    searchState = SearchState.SUGGESTION
                )
            }
            searchJob = viewModelScope.launch {
                delay(300)
                loadSuggestions(keyword)
            }
        }

        // 复位 isSearching
        _uiState.update {
            it.copy(
                isSearching = false
            )
        }

    }

    // 加载推荐结果
    private suspend fun loadSuggestions(keyword: String) {

        // 进入加载状态
        _uiState.update {
            it.copy(
                isSearching = true,
                searchState = SearchState.SUGGESTION
            )
        }

        try{
            val targetQuery = MedicationSearchQuery(
                keyword = keyword
            )

            val response = drugRepository.medicationSearch(targetQuery).getOrThrow()

            _uiState.update {
                it.copy(
                    isSearching = false,
                    suggestions = response.results
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    searchingErrorMessage = e.message ?: "搜索失败"
                )
            }
        }

    }

    // 点击搜索
    fun onSearch() {
        val keyword = _uiState.value.searchKeyword.trim()

        // 空值校验
        if (keyword.isEmpty()) {
            return
        }

        // 清空历史错误信息
        _uiState.update {
            it.copy(
                searchingErrorMessage = null
            )
        }

        // 取消防抖任务
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchState = SearchState.SUGGESTION
                )
            }
            loadSuggestions(keyword)
        }

    }

    // 点击某个推荐结果
    fun onSuggestionsClicked(drugId: Int) {

        // 校验 drugId 合法性
        if (drugId <= 0) {
            _uiState.update {
                it.copy(
                    loadingDrugErrorMessage = "未找到当前药品"
                )
            }
            return
        }

        // 防止重复请求
        if (_uiState.value.isLoadingDrug) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isLoadingDrug = true,
                loadingDrugErrorMessage = null
            )
        }

        // 加载详情
        viewModelScope.launch {
            try {
                val response = drugRepository.getMedicationMessage(drugId).getOrThrow()

                _uiState.update {
                    it.copy(
                        selectedDrug = response,
                        suggestions = emptyList(),
                        searchState = SearchState.HISTORY,
                        searchKeyword = "",
                        searchingErrorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loadingDrugErrorMessage = e.message ?: "加载失败，请稍后再试"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isLoadingDrug = false
                    )
                }
            }
        }

    }

    // 收藏药品
    fun onDrugCollection(drugId: Int) {

        // 防止重复请求
        if (_uiState.value.isCollectDrug) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isCollectDrug = true,
                collectDrugErrorMessage = null
            )
        }

        // 收藏药品
        viewModelScope.launch {
            try {
                drugRepository.addMedicationToCabinet(drugId).getOrThrow()

                _uiState.update {
                    it.copy(
                        selectedDrug = it.selectedDrug.copy(
                            isCollected = true
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        isCollectDrug = false
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCollectDrug = false,
                        collectDrugErrorMessage = e.message ?: "添加至药箱失败"
                    )
                }
            }
        }

    }

}
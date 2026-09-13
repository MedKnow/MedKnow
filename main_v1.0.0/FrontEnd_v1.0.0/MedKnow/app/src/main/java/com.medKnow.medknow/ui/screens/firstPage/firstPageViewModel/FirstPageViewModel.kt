package com.medKnow.medknow.ui.screens.firstPage.firstPageViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.ArticleRepository
import com.medKnow.medknow.data.repository.CheckinRepository
import com.medKnow.medknow.data.repository.DrugRepository
import com.medKnow.medknow.model.checkins.Reminders
import com.medKnow.medknow.model.drugSearch.medicationSearch.MedicationSearchQuery
import com.medKnow.medknow.model.drugSearch.medicationSearch.Results
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryQuery
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.filter

// 首页 ViewModel
@HiltViewModel
class FirstPageViewModel @Inject constructor(
    private val drugRepository: DrugRepository,
    private val checkinRepository: CheckinRepository,
    private val articleRepository: ArticleRepository
): ViewModel() {

    // 定义 FirstPageUiState 数据类
    data class FirstPageUiState (

        // 临近的两条提醒
        val currentReminderPreview: List<Reminders> = emptyList(),
        // 最热两篇科普文章
        val currentTopArticlePreview: List<Article> = emptyList(),
        // 搜索后的药品列表
        val drugSearchedList: List<Results> = emptyList(),
        // 药品搜索关键词
        val searchDrugKeyword: String = "",
        // 是否处于正在搜索的状态
        val isSearching: Boolean = false,
        // 是否正在加载提醒
        val isRemindersLoading: Boolean = false,
        // 是否正在加载文章
        val isArticlesLoading: Boolean = false,
        // 全局错误信息
        val errorMessage: String? = null

    )

    init {
        onReminderPreview()
        onTopArticlePreview()
    }

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(FirstPageUiState())
    val uiState: StateFlow<FirstPageUiState> = _uiState.asStateFlow()

    // 展示当前时间最近的两条提醒
    fun onReminderPreview() {

        // 进入加载状态
        _uiState.update {
            it.copy(
                isRemindersLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {

            // 获取今日距当前时间最近的两条提醒
            val now = LocalTime.now()
            val todayReminders = checkinRepository.getTodayCheckinsList()
                .getOrElse { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isRemindersLoading = false,
                        errorMessage = throwable.message ?: "临近提醒获取失败"
                    )
                    return@launch
                }
                .reminders
                // 选择还未过期的提醒
                .filter { it.scheduledTime.toLocalTime() >= now }
                // 按当天时间排序
                .sortedBy { it.scheduledTime.toLocalTime() }
                .take(2)

            // 结束加载状态
            _uiState.update {
                it.copy(
                    currentReminderPreview = todayReminders,
                    isRemindersLoading = false
                )
            }
        }

    }

    // 展示最热两篇科普文章
    fun onTopArticlePreview() {

        // 进入加载状态
        _uiState.update {
            it.copy(
                isArticlesLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {

            // 获取当前文章列表
            val currentQuery = ArticleListSortByCategoryQuery(
                category = null,
                page = null,
                size = null
            )
            val TopArticles = articleRepository.articleListSortByCategory(currentQuery)
                .getOrElse { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isArticlesLoading = false,
                        errorMessage = throwable.message ?: "最热文章获取失败"
                    )
                    return@launch
                }
                .articles
                .filter { !it.isLiked && !it.isCollected }
                .sortedByDescending { it.readCount}
                .take(2)

            // 结束加载状态
            _uiState.update {
                it.copy(
                    currentTopArticlePreview = TopArticles,
                    isArticlesLoading = false
                )
            }
        }

    }

    // 用户输入关键词
    fun onInputKeyword(keyword: String) {

        _uiState.update {
            it.copy(
                searchDrugKeyword = keyword
            )
        }

    }

    // 用户使用“药品搜索”模块
    fun onDrugSearch() {

        // 防止重复请求
        if (_uiState.value.isSearching) {
            return
        }

        val keyword = _uiState.value.searchDrugKeyword

        // 进入搜索状态
        _uiState.update {
            it.copy(
                isSearching = true,
                errorMessage = null
            )
        }

        // 用户未输入关键词时
        if (keyword.isBlank()) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    drugSearchedList = emptyList()
                )
            }
            return
        }

        // 搜索
        viewModelScope.launch {
            val results = drugRepository.medicationSearch(
                MedicationSearchQuery(keyword = keyword)
            )
            results
                .onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        drugSearchedList = response.results
                    )
                }
            }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            errorMessage = throwable.message ?: "搜索失败"
                        )
                    }

                }
        }
    }

    // 辅助函数

    // 将 "HH:mm" 解析为 LocalTime，遵循 ^([01]\d|2[0-3]):([0-5]\d)$
    private fun String.toLocalTime(): LocalTime =
        LocalTime.parse(this, DateTimeFormatter.ofPattern("HH:mm"))

    // 防止重复导航
    fun onNavigatedToHome() {
        _uiState.update {
            it.copy(
                isRemindersLoading = false,
                isArticlesLoading = false
            )
        }
    }

}
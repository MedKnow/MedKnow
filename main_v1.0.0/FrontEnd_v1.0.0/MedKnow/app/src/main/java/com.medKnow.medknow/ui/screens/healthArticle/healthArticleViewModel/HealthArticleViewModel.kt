package com.medKnow.medknow.ui.screens.healthArticle.healthArticleViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.ArticleRepository
import com.medKnow.medknow.model.healthArticle.ArticleShareRequest
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryQuery
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 定义 ArticleInformationResponse 空值
private val EMPTY_ARTICLE = ArticleInformationResponse(
    articleId = -1,
    author = "",
    category = "",
    coverImage = "",
    isCollected = false,
    isLiked = false,
    likeCount = -1,
    publishTime = "",
    readCount = -1,
    title = "",
    authorTitle = "",
    collectCount = -1,
    content = "",
    relatedArticles = emptyList(),
    shareCount = -1,
    tags = emptyList()
)

// 科普文章页 ViewModel
class HealthArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    // 定义 HealthArticleUiState 数据类
    data class HealthArticleUiState(

        // 文章分类
        val categories: List<String> = listOf("全部", "饮食", "运动", "疾病", "自然"),
        // 用户当前选择的分类
        val selectedCategory: String = "全部",
        // 是否处于加载文章信息状态
        val isLoading: Boolean = false,
        // 是否处于加载文章详情状态
        val isArticleInformationLoading: Boolean = false,
        // 分类选择错误信息
        val categoryErrorMessage: String? = null,
        // 文章详情错误信息
        val articleInformationErrorMessage: String? = null,
        // 分类后的文章
        val sortedArticles: List<Article> = emptyList(),
        // 用户当前选择的文章
        val selectedArticle: ArticleInformationResponse = EMPTY_ARTICLE,
        // 当前页码
        val currentPage: Int = 1,
        // 每页数量
        val pageSize: Int = 3,
        // 是否还能加载更多
        val hasMore: Boolean = true,
        // 是否正在加载更多
        val isLoadingMore: Boolean = false,
        // 是否处于收藏状态
        val isCollectionLoading: Boolean = false,
        // 当前文章是否已收藏
        val isCollected: Boolean = false,
        // 是否处于点赞状态
        val isLikedLoading: Boolean = false,
        // 当前文章是否已点赞
        val isLiked: Boolean = false,
        // 是否处于分享状态
        val isShareLoading: Boolean = false,

    )

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(HealthArticleUiState())
    val uiState: StateFlow<HealthArticleUiState> = _uiState.asStateFlow()

    // 当前选中的分类，作为分类列表请求的触发源
    private val selectedCategoryFlow = MutableStateFlow("全部")

    init {
        observeCategoryChanges()
        // 通过 categoryFlow 触发初始加载
        selectedCategoryFlow.value = "全部"
    }

    // 监听分类变化，flatMapLatest 自动取消上一个在途请求，避免竞态
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeCategoryChanges() {
        viewModelScope.launch {
            selectedCategoryFlow
                .flatMapLatest { category ->
                    flow {
                        // 进入加载状态
                        _uiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                        emit(loadFirstPage(category))
                    }
                }
                .collect { result ->
                    result.onSuccess { articles ->
                        _uiState.update {
                            it.copy(
                                sortedArticles = articles,
                                isLoading = false,
                                categoryErrorMessage = null
                            )
                        }
                    }.onFailure { e ->
                        if (e is CancellationException) {
                            throw e
                        }
                        _uiState.update {
                            it.copy(
                                sortedArticles = emptyList(),
                                isLoading = false,
                                categoryErrorMessage = e.message ?: "文章列表加载失败"
                            )
                        }
                    }
                }
        }
    }

    // 用户选择某种分类（默认为“全部”）
    fun onCategorySelect(category: String) {
        val currentCategory = _uiState.value.selectedCategory

        // 防止无效刷新
        if (currentCategory == category && category == "全部") {
            return
        }

        // 读取用户选择的分类
        val targetCategory = if (category == currentCategory) {
            "全部"
        } else {
            category
        }

        _uiState.update {
            it.copy(
                selectedCategory = targetCategory,
                categoryErrorMessage = null,
                currentPage = 1,
                hasMore = true,
                sortedArticles = emptyList()
            )
        }

        // 更新分类流，触发 flatMapLatest 重新加载
        selectedCategoryFlow.value = targetCategory
    }

    // 用户进入某篇文章查看详情
    fun onArticleSelect(articleId: Int) {
        // 校验 articleId
        if (articleId <= 0) {
            _uiState.update {
                it.copy(articleInformationErrorMessage = "当前文章不存在")
            }
            return
        }

        // 防止重复请求
        if (_uiState.value.isArticleInformationLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isArticleInformationLoading = true,
                articleInformationErrorMessage = null
            )
        }

        // 加载文章内容
        viewModelScope.launch {
            try {
                val resultArticle = articleRepository.articleInformation(articleId).getOrThrow()

                _uiState.update {
                    it.copy(
                        isArticleInformationLoading = false,
                        articleInformationErrorMessage = null,
                        selectedArticle = resultArticle
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        selectedArticle = EMPTY_ARTICLE,
                        articleInformationErrorMessage = e.message ?: "文章内容加载失败"
                    )
                }
            } finally {
                // 兜底复位，确保任何情况都不会卡死 loading
                _uiState.update {
                    it.copy(
                        isArticleInformationLoading = false
                    )
                }
            }
        }

    }

    // 加载文章列表第一页
    private suspend fun loadFirstPage(category: String): Result<List<Article>> {
        return try{
            if(category == "全部") {
                val response = articleRepository.recommendedArticleList().getOrThrow()
                _uiState.update {
                    it.copy(
                        sortedArticles = response.articles
                    )
                }
                Result.success(response.articles)
            } else {
                val query = ArticleListSortByCategoryQuery(
                    category = category,
                    page = 1,
                    size = _uiState.value.pageSize
                )
                val response = articleRepository.articleListSortByCategory(query).getOrThrow()
                _uiState.update {
                    it.copy(
                        sortedArticles = response.articles
                    )
                }
                Result.success(response.articles)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 加载下一页
    fun loadMore() {
        val state = _uiState.value

        // 防止重复请求并且判断是否有更多文章
        if (state.isLoadingMore || !state.hasMore || state.isLoading) {
            return
        }

        // “全部”采用推荐列表，仅展示第一页
        if (state.selectedCategory == "全部") {
            _uiState.update {
                it.copy(
                    hasMore = false
                )
            }
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isLoadingMore = true
            )
        }

        viewModelScope.launch {
            try {

                val nextPage = state.currentPage + 1

                val query = ArticleListSortByCategoryQuery(
                    category = state.selectedCategory,
                    page = nextPage,
                    size = state.pageSize
                )

                val response = articleRepository.articleListSortByCategory(query).getOrThrow()

                _uiState.update {
                    it.copy(
                        sortedArticles = it.sortedArticles + response.articles,
                        currentPage = nextPage,
                        hasMore = response.page < response.totalPages,
                        isLoadingMore = false
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        categoryErrorMessage = e.message ?: "加载更多失败"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false
                    )
                }
            }
        }

    }

    // 用户收藏或取消收藏文章
    fun onArticleCollection() {

        val currentArticle = _uiState.value.selectedArticle

        // 防止重复请求
        if (_uiState.value.isCollectionLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isCollectionLoading = true,
                isCollected = !currentArticle.isCollected,
                selectedArticle = it.selectedArticle.copy(isCollected =  !currentArticle.isCollected)
            )
        }

        // 收藏或取消收藏
        viewModelScope.launch {
            try {
                articleRepository.articleCollect(currentArticle.articleId).getOrThrow()

                _uiState.update {
                    it.copy(
                        isCollectionLoading = false,
                        articleInformationErrorMessage = null,
                        selectedArticle = it.selectedArticle.copy(
                            collectCount = if (it.isCollected) {
                                currentArticle.collectCount + 1
                            } else {
                                currentArticle.collectCount - 1
                            }
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCollectionLoading = false,
                        isCollected = currentArticle.isCollected,
                        selectedArticle = it.selectedArticle.copy(isCollected = currentArticle.isCollected),
                        articleInformationErrorMessage = e.message ?: "收藏失败"
                    )
                }
            }
        }

    }

    // 用户点赞或取消点赞文章
    fun onArticleLike() {

        val currentArticle = _uiState.value.selectedArticle

        // 防止重复请求
        if (_uiState.value.isLikedLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isLikedLoading = true,
                isLiked = !currentArticle.isLiked,
                selectedArticle = it.selectedArticle.copy( isLiked = !currentArticle.isLiked)
            )
        }

        // 点赞或取消点赞
        viewModelScope.launch {
            try {
                articleRepository.articleLike(currentArticle.articleId).getOrThrow()

                _uiState.update {
                    it.copy(
                        isLikedLoading = false,
                        articleInformationErrorMessage = null,
                        selectedArticle = it.selectedArticle.copy(
                            likeCount = if (it.isLiked) {
                                currentArticle.likeCount + 1
                            } else {
                                currentArticle.likeCount - 1
                            }
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLikedLoading = false,
                        isLiked = currentArticle.isLiked,
                        selectedArticle = it.selectedArticle.copy(isLiked = currentArticle.isLiked),
                        articleInformationErrorMessage = e.message ?: "点赞或取消点赞操作失败"
                    )
                }
            }
        }

    }

    // 分享文章
    fun onArticleShare(platform: String) {

        val currentArticle = _uiState.value.selectedArticle

        // 防止重复请求
        if (_uiState.value.isShareLoading) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isShareLoading = true
            )
        }

        // 分享文章
        viewModelScope.launch {
            try {
                val targetRequest = ArticleShareRequest(platform)
                articleRepository.articleShare(currentArticle.articleId, targetRequest).getOrThrow()

                _uiState.update {
                    it.copy(
                        isShareLoading = false,
                        articleInformationErrorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isShareLoading = false,
                        articleInformationErrorMessage = e.message ?: "分享失败"
                    )
                }
            }
        }
    }

    // 辅助函数

    // 解决重复导航问题
    fun onNavigatedToHome() {
        _uiState.update {
            it.copy(
                isLoading = false,
                isArticleInformationLoading = false,
                isLoadingMore = false
            )
        }
    }

}
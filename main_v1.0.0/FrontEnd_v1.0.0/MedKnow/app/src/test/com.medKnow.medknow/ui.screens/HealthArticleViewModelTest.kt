package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeArticleRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeErrorArticleFlags
import com.medKnow.medknow.ui.screens.healthArticle.healthArticleViewModel.HealthArticleViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

// 科普文章 ViewModel 单元测试
@OptIn(ExperimentalCoroutinesApi::class)
class HealthArticleViewModelTest {

    // 替换 Dispatcher.Main
    private val mainDispatcherRule =  MainDispatcherRule()

    // 初始化假仓库
    private val fakeArticleRepository = FakeArticleRepository()
    private fun createViewModel() = HealthArticleViewModel(fakeArticleRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertFalse(state.isLoadingMore)
        assertTrue(state.hasMore)
        assertFalse(state.isLiked)
        assertFalse(state.isCollected)
        assertFalse(state.isCollectionLoading)
        assertFalse(state.isArticleInformationLoading)
        assertFalse(state.isLikedLoading)
        assertFalse(state.isCollectionLoading)
        assertFalse(state.isShareLoading)
        assertEquals(listOf("全部", "饮食", "运动", "疾病", "自然"), state.categories)
        assertEquals("全部", state.selectedCategory)
        assertNull(state.categoryErrorMessage)
        assertNull(state.articleInformationErrorMessage)
    }

    // 选择某个分类
    @Test
    fun onCategorySelected() = runTest {
        val viewModel = createViewModel()
        viewModel.onCategorySelect("饮食")
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("饮食", state.selectedCategory)
        assertNull(state.categoryErrorMessage)
    }

    // 点击同一分类取消选择
    @Test
    fun onCancelCategorySelect() = runTest {
        val viewModel = createViewModel()
        viewModel.onCategorySelect("饮食")
        viewModel.onCategorySelect("饮食")
        advanceUntilIdle()

        assertEquals("全部", viewModel.uiState.value.selectedCategory)
    }

    // 选择无效的文章（非法 articleId ）
    @Test
    fun onArticleSelectInvalidId() {
        val viewModel = createViewModel()

        viewModel.onArticleSelect(-1)

        val state = viewModel.uiState.value
        assertEquals("当前文章不存在", state.articleInformationErrorMessage)
        assertFalse(state.isArticleInformationLoading)
    }

    // 选择有效文章
    @Test
    fun onArticleSelectValidId() = runTest {
        val viewModel = createViewModel()
        viewModel.onArticleSelect(3031)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isArticleInformationLoading)
        assertNull(state.articleInformationErrorMessage)
        assertEquals(3031, state.selectedArticle.articleId)
    }

    // 收藏文章
    @Test
    fun onArticleCollect() = runTest {
        val viewModel = createViewModel()
        viewModel.onArticleSelect(3031)
        advanceUntilIdle()

        viewModel.onArticleCollection()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCollectionLoading)
        assertTrue(state.isCollected)
        assertTrue(state.selectedArticle.isCollected)
    }

    // 点赞文章
    @Test
    fun onArticleLike() = runTest {
        val viewModel = createViewModel()
        viewModel.onArticleSelect(3031)
        advanceUntilIdle()

        viewModel.onArticleLike()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLikedLoading)
        assertFalse(state.isLiked)
        assertFalse(state.selectedArticle.isLiked)
    }

    // 分享文章
    @Test
    fun onArticleShare() = runTest {
        val viewModel = createViewModel()
        viewModel.onArticleSelect(3031)
        advanceUntilIdle()

        viewModel.onArticleShare("WeChat")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isShareLoading)
        assertNull(state.articleInformationErrorMessage)
    }

    // 收藏时服务器错误
    @Test
    fun onArticleCollectWithServerError() = runTest {
        val viewModel = createViewModel()
        viewModel.onArticleSelect(3031)
        advanceUntilIdle()

        FakeErrorArticleFlags.simulateServerError = true
        try {
            viewModel.onArticleCollection()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isCollectionLoading)
            assertNull(state.articleInformationErrorMessage)
        } finally {
            FakeErrorArticleFlags.simulateServerError = false
        }
    }
}
package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeArticleRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeCheckinRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeDrugRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeErrorDrugFlags
import com.medKnow.medknow.ui.screens.firstPage.firstPageViewModel.FirstPageViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 首页 ViewModel 单元测试
@OptIn(ExperimentalCoroutinesApi::class)
class FirstPageViewModelTest {

    // 替换 Dispatcher.Main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeDrugRepository = FakeDrugRepository()
    private val fakeCheckinRepository = FakeCheckinRepository()
    private val fakeArticleRepository = FakeArticleRepository()
    private fun createViewModel() = FirstPageViewModel(
        fakeDrugRepository,
        fakeCheckinRepository,
        fakeArticleRepository
    )

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isSearching)
        assertFalse(state.isRemindersLoading)
        assertFalse(state.isArticlesLoading)
        assertNull(state.errorMessage)
        assertTrue(state.currentReminderPreview.isEmpty())
        assertTrue(state.currentTopArticlePreview.isEmpty())
        assertTrue(state.drugSearchedList.isEmpty())
        assertEquals("", state.searchDrugKeyword)
    }

    // 成功加载今日提醒
    @Test
    fun onReminderPreviewCompletes() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRemindersLoading)
        assertNull(state.errorMessage)
    }

    // 成功加载最热文章
    @Test
    fun onTopArticlePreviewCompletes() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isArticlesLoading)
        assertNull(state.errorMessage)
    }

    // 输入关键词
    @Test
    fun onInputKeyword() {
        val viewModel = createViewModel()
        viewModel.onInputKeyword("阿莫西林")

        assertEquals("阿莫西林", viewModel.uiState.value.searchDrugKeyword)
    }

    // 关键词为空但点击搜索时
    @Test
    fun onSearchWithEmptyKeyword() = runTest {
        val viewModel = createViewModel()
        viewModel.onInputKeyword("   ")
        viewModel.onDrugSearch()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertTrue(state.drugSearchedList.isEmpty())
        assertNull(state.errorMessage)
    }

    // 搜索成功
    @Test
    fun onSearchSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.onInputKeyword("阿莫西林")
        viewModel.onDrugSearch()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNull(state.errorMessage)
        assertEquals(1, state.drugSearchedList.size)
        assertEquals("阿莫西林胶囊", state.searchDrugKeyword)
    }

    // 搜索失败（服务器错误）
    @Test
    fun onSearchWithServerError() = runTest {
        val viewModel = createViewModel()
        viewModel.onInputKeyword("阿莫西林")
        viewModel.onDrugSearch()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals("系统繁忙。请稍后再试", state.errorMessage)
        assertTrue(state.drugSearchedList.isEmpty())

        FakeErrorDrugFlags.simulateServerError = false
    }

    // 重置状态，清空导航
    @Test
    fun onClearNavigation() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onNavigatedToHome()

        val state = viewModel.uiState.value
        assertFalse(state.isRemindersLoading)
        assertFalse(state.isArticlesLoading)
    }
}
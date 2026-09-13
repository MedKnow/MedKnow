package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeDrugRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeErrorDrugFlags
import com.medKnow.medknow.ui.screens.drugSearch.drugSearchViewModel.DrugSearchViewModel
import com.medKnow.medknow.ui.screens.drugSearch.drugSearchViewModel.EMPTY_MEDICATION
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 药品搜索页 viewModel 单元测试
@OptIn(ExperimentalCoroutinesApi::class)
class DrugSearchViewModelTest {

    // 替换 Dispatcher.Main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeDrugRepository = FakeDrugRepository()

    private fun createViewModel() = DrugSearchViewModel(fakeDrugRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isSearching)
        assertFalse(state.isLoadingDrug)
        assertFalse(state.isCollectDrug)
        assertNull(state.searchingErrorMessage)
        assertNull(state.loadingDrugErrorMessage)
        assertNull(state.collectDrugErrorMessage)
        assertEquals("", state.searchKeyword)
        assertEquals(DrugSearchViewModel.SearchState.HISTORY, state.searchState)
        assertTrue(state.suggestions.isEmpty())
        assertEquals(EMPTY_MEDICATION, state.selectedDrug)
    }

    // 未输入关键词
    @Test
    fun onNullSearchKeyword() {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("")

        val state = viewModel.uiState.value
        assertEquals("", state.searchKeyword)
        assertEquals(DrugSearchViewModel.SearchState.HISTORY, state.searchState)
        assertTrue(state.suggestions.isEmpty())
        assertFalse(state.isSearching)
    }

    // 已输入关键词
    @Test
    fun onNotNullSearchKeyword() {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("阿莫西林")

        val state = viewModel.uiState.value
        assertEquals("阿莫西林", state.searchKeyword)
        assertEquals(DrugSearchViewModel.SearchState.SUGGESTION, state.searchState)
        assertNull(state.searchingErrorMessage)
    }

    // 加载推荐结果
    @Test
    fun onLoadSuggestions() = runTest {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("阿莫西林")
        advanceTimeBy(400)

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(1, state.suggestions.size)
        assertEquals("阿莫西林胶囊", state.suggestions[0].drugName)
        assertNull(state.searchingErrorMessage)
    }

    // 输入关键词无推荐
    @Test
    fun onKeywordWithNoSuggestions() = runTest {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("不存在的药")
        advanceTimeBy(400)

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertNull(state.searchingErrorMessage)
        assertTrue(state.suggestions.isEmpty())
    }

    // 搜索成功
    @Test
    fun onSearchSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("阿莫西林")
        viewModel.onSearch()
        advanceTimeBy(500)

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(1, state.suggestions.size)
        assertNull(state.searchingErrorMessage)
    }

    // 点击搜索，关键词为空时不触发
    @Test
    fun onSearchAndUnreachableWithNullKeyword() = runTest {
        val viewModel = createViewModel()
        viewModel.onSearch()

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(DrugSearchViewModel.SearchState.HISTORY, state.searchState)
        assertTrue(state.suggestions.isEmpty())
    }

    // 搜索失败（服务器错误）
    @Test
    fun onSearchUnreachableWithServerError() = runTest {
        val viewModel = createViewModel()
        viewModel.onKeywordChange("阿莫西林")
        viewModel.onSearch()
        advanceTimeBy(500)

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals("系统繁忙，请稍后再试", state.searchingErrorMessage)
        assertTrue(state.suggestions.isEmpty())

        FakeErrorDrugFlags.simulateServerError = false
    }

    // 点击推荐结果查看详情
    @Test
    fun onClickSuggestions() = runTest {
        val viewModel = createViewModel()
        viewModel.onSuggestionsClicked(10001)
        advanceTimeBy(600)

        val state = viewModel.uiState.value
        assertFalse(state.isLoadingDrug)
        assertNull(state.loadingDrugErrorMessage)
        assertEquals(10001, state.selectedDrug.drugId)
        assertEquals("阿莫西林胶囊", state.selectedDrug.drugName)
        assertEquals(DrugSearchViewModel.SearchState.HISTORY, state.searchState)
        assertEquals("", state.searchKeyword)
        assertTrue(state.suggestions.isEmpty())
    }

    // 查看详情失败（非法 id）
    @Test
    fun onClickSuggestionsWithIllegalDrugId() {
        val viewModel = createViewModel()
        viewModel.onSuggestionsClicked(-1)

        val state = viewModel.uiState.value
        assertEquals("未找到当前药品", state.loadingDrugErrorMessage)
        assertFalse(state.isLoadingDrug)
    }

    // 查看药品详情（药品不存在）
    @Test
    fun onClickSuggestionsWithNullDrug() = runTest {
        val viewModel = createViewModel()
        viewModel.onSuggestionsClicked(9999)

        val state = viewModel.uiState.value
        assertEquals("药品不存在", state.loadingDrugErrorMessage)
        assertFalse(state.isLoadingDrug)
    }

    // 收藏药品且成功
    @Test
    fun onCollectDrugSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.onSuggestionsClicked(10001)
        advanceTimeBy(600)
        viewModel.onDrugCollection(10001)
        advanceTimeBy(300)

        val state = viewModel.uiState.value
        assertFalse(state.isCollectDrug)
        assertNull(state.collectDrugErrorMessage)
        assertTrue(state.selectedDrug.isCollected)
    }

    // 收藏药品失败（重复收藏）
    @Test
    fun onCollectDrugAgain() = runTest {
        val viewModel = createViewModel()
        viewModel.onSuggestionsClicked(10001)
        advanceTimeBy(600)
        viewModel.onDrugCollection(10001)
        advanceTimeBy(300)

        val state = viewModel.uiState.value
        assertTrue(state.selectedDrug.isCollected)

        viewModel.onDrugCollection(10001)
        advanceTimeBy(300)

        assertFalse(state.isCollectDrug)
        assertEquals("该药品已在药箱中", state.collectDrugErrorMessage)
    }
}
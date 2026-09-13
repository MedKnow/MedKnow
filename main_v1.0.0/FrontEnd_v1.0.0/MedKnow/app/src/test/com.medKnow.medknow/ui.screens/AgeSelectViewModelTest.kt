package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeUserRepository
import com.medKnow.medknow.ui.screens.ageSelect.ageSelectViewModel.AgeSelectViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


// 年龄选择页 ViewModel 单元测试
class AgeSelectViewModelTest {

    // 替换 Dispatchers.Main
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeUserRepository = FakeUserRepository()

    private fun createViewModel() = AgeSelectViewModel(fakeUserRepository)

    // 初始状态
    @Test
    fun startState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals(listOf(0, 18, 60), state.age)
        assertFalse(state.isSubmitting)
        assertFalse(state.isSaved)
        assertNull(state.errorMessage)
    }

    // 选择年龄段
    @Test
    fun onAgeSelect() {
        val viewModel = createViewModel()
        viewModel.onAgeSelected(18)

        assertEquals(18, viewModel.uiState.value.selectedAge)
    }

    // 点击同一年龄段取消选择
    @Test
    fun onCancelAgeSelect() {
        val viewModel = createViewModel()
        viewModel.onAgeSelected(18)
        viewModel.onAgeSelected(18)

        assertEquals(-1, viewModel.uiState.value.selectedAge)
    }

    // 选择不同的年龄段
    @Test
    fun onChangeAgeSelect() {
        val viewModel = createViewModel()
        viewModel.onAgeSelected(18)
        viewModel.onAgeSelected(60)

        assertEquals(60, viewModel.uiState.value.selectedAge)
    }

    // 未选择年龄段
    @Test
    fun onAgeUnSelected() = runTest {
        val viewModel = createViewModel()

        viewModel.saveAge()

        val state = viewModel.uiState.value
        assertEquals("请选择一种年龄段", state.errorMessage)
        assertFalse(state.isSaved)
        assertFalse(state.isSubmitting)
    }

    // 选择完后保存信息且保存成功
    @Test
    fun onAgeSaveSuccess()  = runTest {
        val viewModel = createViewModel()
        viewModel.onAgeSelected(18)
        viewModel.saveAge()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isSubmitting)
        assertNull(state.errorMessage)
    }

    // 重置导航
    @Test
    fun onClearNavigation() = runTest {
        val viewModel = createViewModel()
        viewModel.saveAge()
        assertTrue(viewModel.uiState.value.isSaved)

        viewModel.onNavigatedToHome()

        assertFalse(viewModel.uiState.value.isSaved)
    }
}
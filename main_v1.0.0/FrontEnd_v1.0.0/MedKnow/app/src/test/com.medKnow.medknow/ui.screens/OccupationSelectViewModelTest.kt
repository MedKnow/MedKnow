package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeUserRepository
import com.medKnow.medknow.ui.screens.occupationSelect.occupationSelectViewModel.OccupationSelectViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

// 职业选择页 ViewModel 单元测试
class OccupationSelectViewModelTest {

    // 替换 Dispatcher.main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeUserRepository = FakeUserRepository()
    private fun createViewModel() = OccupationSelectViewModel(fakeUserRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isSaved)
        assertFalse(state.isSubmitting)
        assertNull(state.errorMessage)
        assertEquals(
            listOf(
            "医生", "学生", "老师", "码农", "工人",
            "律师", "骑手", "退休", "警察", "其他"
            ),
            state.occupation
        )
        assertEquals("", state.selectedOccupation)
    }

    // 选择某个职业
    @Test
    fun onOccupationSelect() {
        val viewModel = createViewModel()

        viewModel.onOccupationSelected("医生")

        assertEquals("医生", viewModel.uiState.value.selectedOccupation)
    }

    // 点击选择的职业取消选择
    @Test
    fun onCancelOccupationSelect() {
        val viewModel = createViewModel()

        viewModel.onOccupationSelected("医生")
        viewModel.onOccupationSelected("医生")

        assertEquals("", viewModel.uiState.value.selectedOccupation)
    }

    // 切换到其他职业
    @Test
    fun onChangeOccupationSelect() {
        val viewModel = createViewModel()

        viewModel.onOccupationSelected("医生")
        viewModel.onOccupationSelected("老师")

        assertEquals("老师", viewModel.uiState.value.selectedOccupation)
    }

    // 未选择职业时点击下一步
    @Test
    fun onSaveOnEmptyOccupation() {
        val viewModel = createViewModel()

        viewModel.saveOccupation()

        val state = viewModel.uiState.value
        assertEquals("请选择一个职业", state.errorMessage)
        assertFalse(state.isSaved)
    }

    // 保存成功
    @Test
    fun onSaveOccupationSuccess() {
        val viewModel = createViewModel()

        viewModel.onOccupationSelected("医生")
        viewModel.saveOccupation()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertFalse(state.isSubmitting)
        assertNull(state.errorMessage)
    }

    // 重置导航
    @Test
    fun onClearNavigation() {
        val viewModel = createViewModel()

        viewModel.onOccupationSelected("医生")
        viewModel.saveOccupation()
        viewModel.onNavigatedToHome()

        val state = viewModel.uiState.value
        assertFalse(state.isSaved)
    }
}
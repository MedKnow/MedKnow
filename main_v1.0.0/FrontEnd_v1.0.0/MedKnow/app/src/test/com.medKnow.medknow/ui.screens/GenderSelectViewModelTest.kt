package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeErrorUserFlags
import com.medKnow.medknow.data.repository.fakeRepository.FakeUserRepository
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.ui.screens.genderSelect.genderSelectViewModel.GenderSelectViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 性别选择页 ViewModel 单元测试
class GenderSelectViewModelTest {

    // 替换 Dispatcher.Main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeUserRepository = FakeUserRepository()
    private fun createViewModel() = GenderSelectViewModel(fakeUserRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isSubmitting)
        assertFalse(state.isSaved)
        assertNull(state.errorMessage)
        assertEquals(listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER), state.gender)
        assertEquals(Gender.OTHER, state.selectedGender)
    }

    // 选择某个性别
    @Test
    fun onGenderSelected() {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)

        assertEquals(Gender.MALE, viewModel.uiState.value.selectedGender)
    }

    // 点击已选择性别取消选择
    @Test
    fun onCancelGenderSelect() {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)
        viewModel.onGenderSelected(Gender.MALE)

        assertEquals(Gender.OTHER, viewModel.uiState.value.selectedGender)
    }

    // 切换性别选择
    @Test
    fun onChangeGenderSelect() {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)
        viewModel.onGenderSelected(Gender.FEMALE)

        assertEquals(Gender.FEMALE, viewModel.uiState.value.selectedGender)
    }

    // 未选择时保存
    @Test
    fun onSaveWithEmptyGender() = runTest {
        val viewModel = createViewModel()
        viewModel.saveGender()

        val state = viewModel.uiState.value
        assertFalse(state.isSaved)
        assertEquals("请选择一种性别", state.errorMessage)
    }

    // 选择后保存成功
    @Test
    fun onSaveGenderSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)
        viewModel.saveGender()

        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertTrue(state.isSaved)
        assertNull(state.errorMessage)
    }

    // 保存时服务器错误
    @Test
    fun onSaveGenderWithServerError() = runTest {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)

        FakeErrorUserFlags.simulateServerError = true

        try {
            viewModel.saveGender()

            val state = viewModel.uiState.value
            assertFalse(state.isSaved)
            assertNull(state.errorMessage)
        } finally {
            FakeErrorUserFlags.simulateServerError = false
        }
    }

    // 重置导航
    @Test
    fun onClearNavigation() {
        val viewModel = createViewModel()
        viewModel.onGenderSelected(Gender.MALE)
        viewModel.saveGender()
        assertTrue(viewModel.uiState.value.isSaved)

        viewModel.onNavigatedToHome()

        assertFalse(viewModel.uiState.value.isSaved)
    }
}
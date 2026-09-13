package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeCommonRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeUserRepository
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.EMPTY_FEEDBACK
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.EMPTY_USER
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.UserPageViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.File

// 个人中心页 ViewModel 单元测试
class UserPageViewModelTest {

    // 替换Disptcher.main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeUserRepository = FakeUserRepository()
    private val fakeCommonRepository = FakeCommonRepository()
    private fun createViewModel() = UserPageViewModel(fakeUserRepository, fakeCommonRepository)

    // 初始状态
    @Test
    fun onStartState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoadingUserMessage)
        assertFalse(state.isUpdatingAvatar)
        assertFalse(state.isUpdatingUserInformation)
        assertFalse(state.isLoading)
        assertFalse(state.isGettingUserInformation)
        assertFalse(state.isSubmittingFeedback)
        assertNull(state.loadingUserErrorMessage)
        assertNull(state.updatingAvatarErrorMessage)
        assertNull(state.updatingUserInformationErrorMessage)
        assertNull(state.gettingUserInformationErrorMessage)
        assertNull(state.submittingFeedbackErrorMessage)
        assertEquals(EMPTY_USER, state.currentUserInformation)
        assertEquals(EMPTY_FEEDBACK, state.currentUserFeedback)
    }

    // 自动加载用户信息
    @Test
    fun initLoadUserInformation() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertFalse(state.isLoadingUserMessage)
        assertFalse(state.isLoading)
        assertNull(state.loadingUserErrorMessage)
        assertEquals("周易权", state.currentUserInformation.userName)
        assertEquals("1001", state.currentUserInformation.userId)
        assertEquals(30, state.currentUserInformation.age)
        assertEquals(Gender.MALE, state.currentUserInformation.gender)
    }

    // 加载信息成功
    @Test
    fun onLoadUserInformationSuccess() {
        val viewModel = createViewModel()

        viewModel.onLoadUserInformation()

        val state = viewModel.uiState.value
        assertFalse(state.isLoadingUserMessage)
        assertNull(state.loadingUserErrorMessage)
        assertEquals("周易权", state.currentUserInformation.userName)
    }

    // 更换头像成功
    @Test
    fun onChangeAvatarSuccess() {
        val viewModel = createViewModel()

        viewModel.onChangeAvatar(File("avatr.png"))

        val state = viewModel.uiState.value
        assertFalse(state.isUpdatingAvatar)
        assertNull(state.updatingAvatarErrorMessage)
        assertEquals("http://localhost:8090/default.png", state.currentUserInformation.avatar)
    }

    // 更新用户信息成功
    @Test
    fun onUpdateUserInformationSuccess() {
        val viewModel = createViewModel()

        viewModel.onUpdateUserInformation(
            userName = "新名字",
            age = 40,
            gender = Gender.FEMALE,
            occupation = "教师",
            allergies = "无",
            chronicDiseases = "无"
        )

        val state = viewModel.uiState.value
        assertFalse(state.isUpdatingUserInformation)
        assertNull(state.updatingUserInformationErrorMessage)
        assertEquals("新名字", state.currentUserInformation.userName)
        assertEquals(40, state.currentUserInformation.age)
        assertEquals(Gender.FEMALE, state.currentUserInformation.gender)
        assertEquals("教师", state.currentUserInformation.occupation)
    }

    // 更新信息未传参数时采用原值
    @Test
    fun onUpdateUserInformationWithEmptyValueUseOrigin() {
        val viewModel = createViewModel()

        viewModel.onUpdateUserInformation(
            userName = null,
            age = null,
            gender = null,
            occupation = null,
            allergies = null,
            chronicDiseases = null
        )

        val state = viewModel.uiState.value
        assertEquals("周易权", state.currentUserInformation.userName)
        assertEquals(30, state.currentUserInformation.age)
        assertEquals(Gender.MALE, state.currentUserInformation.gender)
    }

    // 查看信息成功
    @Test
    fun onCheckUserInformationSuccess() {
        val viewModel = createViewModel()

        viewModel.onGetUserInformation()

        val state = viewModel.uiState.value
        assertFalse(state.isGettingUserInformation)
        assertNull(state.gettingUserInformationErrorMessage)
        assertEquals("周易权", state.currentUserInformation.userName)
        assertEquals("花粉过敏", state.currentUserInformation.allergies)
    }

    // 提交反馈成功
    @Test
    fun onSubmitFeedbackSuccess() {
        val viewModel = createViewModel()

        viewModel.onSubmittingFeedback(
            content = "这个应用很好",
            contact = "13800000000",
            images = listOf("http://localhost:8090/img1.png")
        )

        val state = viewModel.uiState.value
        assertFalse(state.isSubmittingFeedback)
        assertNull(state.submittingFeedbackErrorMessage)
        assertEquals(EMPTY_FEEDBACK, state.currentUserFeedback)
    }

    // 提交空反馈
    @Test
    fun onSubmitFeedbackWithEmptyFeedback() {
        val viewModel = createViewModel()

        viewModel.onSubmittingFeedback(
            content = "",
            contact = null,
            images = null
        )

        val state = viewModel.uiState.value
        assertFalse(state.isSubmittingFeedback)
        assertNotNull(state.submittingFeedbackErrorMessage)
    }

    // 重置导航
    @Test
    fun onClearNavigation() {
        val viewModel = createViewModel()

        viewModel.onNavigatedToHome()

        assertFalse(viewModel.uiState.value.isLoading)
    }
}
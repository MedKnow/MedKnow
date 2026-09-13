package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.repository.fakeRepository.FakeAuthRepository
import com.medKnow.medknow.data.repository.fakeRepository.FakeErrorUserFlags
import com.medKnow.medknow.data.repository.fakeRepository.FakeUserRepository
import com.medKnow.medknow.ui.screens.signup.signupViewModel.SignupViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 注册页 ViewModel 单元测试
class SignupViewModelTest {

    // 替换 Dispatchers.Main
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 初始化假仓库
    private val fakeAuthRepository = FakeAuthRepository()
    private val fakeUserRepository = FakeUserRepository()

    private fun createViewModel() = SignupViewModel(fakeAuthRepository, fakeUserRepository)

    // 辅助函数
    // 填入合法的手机号、验证码、用户名
    private fun SignupViewModel.fillValidInput() {
        onUserNameChanged("周易权")
        onUserPhoneChanged("13800000000")
        onUserCodeChanged("123456")
    }

    // 手机号输入校验
    // 初始状态
    @Test
    fun startState() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.userName)
        assertEquals("", state.userPhone)
        assertEquals("", state.userCode)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    // 1. 输入时校验

    // 输入手机号
    @Test
    fun onUserPhoneInput() {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("13800000000")

        assertEquals("13800000000", viewModel.uiState.value.userPhone)
    }

    // 手机号空值校验
    @Test
    fun onCheckUserPhoneIsNull() {
        val viewModel = createViewModel()
        viewModel.onUserCodeChanged("123456")
        viewModel.onUserNameChanged("周易权")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("手机号不能为空", viewModel.uiState.value.userPhoneErrorMessage)
    }

    // 手机号长度校验
    @Test
    fun onCheckUserPhoneLengthIsLegal() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("123")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("手机号格式不正确", viewModel.uiState.value.userPhoneErrorMessage)
    }

    // 手机号内容校验
    @Test
    fun onCheckUserPhoneContentIsLegal() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("123#")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("手机号只能包含数字", viewModel.uiState.value.userPhoneErrorMessage)
    }

    // 验证码空值校验
    @Test
    fun onCheckUserCodeIsNull() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("13800000000")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("验证码不能为空", viewModel.uiState.value.userCodeErrorMessage)
    }

    // 用户名空值校验
    @Test
    fun onCheckUserNameIsNull() {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("13800000000")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("用户名不能为空", viewModel.uiState.value.userNameErrorMessage)
    }

    // 用户名长度校验
    @Test
    fun onCheckUserNameLengthIsLegal() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("1")
        viewModel.onUserPhoneChanged("13800000000")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("用户名长度应保持在2到20个字符之间", viewModel.uiState.value.userNameErrorMessage)
    }

    // 验证码内容校验
    @Test
    fun onCheckUserCodeContentIsLegal() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("13800000000")
        viewModel.onUserCodeChanged("12345")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("验证码错误", viewModel.uiState.value.userCodeErrorMessage)
    }

    // 2. 点击跳转时校验
    // 显示验证码错误
    @Test
    fun userCodeContentIllegal() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("13800000000")
        viewModel.onUserCodeChanged("000000")

        viewModel.onNavigateToNextStepClicked()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isNavigateSuccess)
        assertEquals("验证码错误", state.errorMessage)
    }

    // 手机号未注册
    @Test
    fun userPhoneUnSignup() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("13900000000")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isNavigateSuccess)
        assertEquals("手机号未注册", state.errorMessage)
    }

    // 注册成功
    @Test
    fun signupSuccess() {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")
        viewModel.onUserPhoneChanged("13800000000")
        viewModel.onUserCodeChanged("123456")

        viewModel.onNavigateToNextStepClicked()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isNavigateSuccess)
        assertNull(state.errorMessage)
    }

    // 3. 跳转后信息保存校验
    // 未完成验证
    @Test
    fun unAuthority() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserNameChanged("周易权")

        viewModel.onNavigateToNextStepClicked()

        assertEquals("请先完成验证", viewModel.uiState.value.errorMessage)
    }

    // 信息保存成功
    @Test
    fun saveSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.fillValidInput()

        // 验证
        viewModel.onNavigateToNextStepClicked()
        assertTrue(viewModel.uiState.value.isNavigateSuccess)

        // 保存
        viewModel.onSaveSignupMessage()
        val state = viewModel.uiState.value
        assertFalse(state.isSubmitting)
        assertTrue(state.isSaved)
        assertNull(state.errorMessage)
    }

    // 保存时服务器错误
    @Test
    fun savingWithServerError() = runTest {
        val viewModel = createViewModel()
        viewModel.fillValidInput()
        viewModel.onNavigateToNextStepClicked()

        // 模拟服务器错误
        FakeErrorUserFlags.simulateServerError = true
        try {
            viewModel.onSaveSignupMessage()

            val state = viewModel.uiState.value
            assertFalse(state.isSaved)
            assertFalse(state.isSubmitting)
            assertNotNull(state.errorMessage)
        } finally {
            FakeErrorUserFlags.simulateServerError = false
        }

    }

    // 4. 重置导航辅助函数
    @Test
    fun clearNavigation() = runTest {
        val viewModel = createViewModel()
        viewModel.fillValidInput()
        viewModel.onNavigateToNextStepClicked()
        viewModel.onSaveSignupMessage()

        assertTrue(viewModel.uiState.value.isSaved)

        viewModel.onNavigatedToHome()

        val state = viewModel.uiState.value
        assertFalse(state.isNavigateSuccess)
        assertFalse(state.isSaved)
    }

}
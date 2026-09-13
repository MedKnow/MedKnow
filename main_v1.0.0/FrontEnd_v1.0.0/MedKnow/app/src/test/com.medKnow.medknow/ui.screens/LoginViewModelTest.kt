package com.medKnow.medknow.ui.screens

import com.medKnow.medknow.MainDispatcherRule
import com.medKnow.medknow.data.local.TokenStore
import com.medKnow.medknow.data.repository.fakeRepository.FakeAuthRepository
import com.medKnow.medknow.ui.screens.login.loginViewModel.LoginViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

// 登录页 ViewModel 单元测试
class LoginViewModelTest {

    // 替换 Dispatcher.Main
    @get:Rule
    private val mainDispatcherRule = MainDispatcherRule()
    private val tokenStore: TokenStore = mockk(relaxed = true)

    // 初始化假仓库
    private val fakeAuthRepository = FakeAuthRepository()
    private fun createViewModel() = LoginViewModel(fakeAuthRepository, tokenStore)

    // 输入手机号后清除对应错误信息
    @Test
    fun onPhoneInput() = runTest {
        val viewModel = createViewModel()

        viewModel.onLoginClicked()
        viewModel.onUserPhoneChanged("1235678900")

        assertEquals("12345678900", viewModel.uiState.value.userPhone)
        assertNull(viewModel.uiState.value.userPhoneErrorMessage)
    }

    // 输入验证码后清除对应错误信息
    @Test
    fun onUserCodeInput() = runTest {
        val viewModel = createViewModel()

        viewModel.onUserCodeChanged("123456")

        assertNull(viewModel.uiState.value.userCodeErrorMessage)
        assertEquals("123456", viewModel.uiState.value.userCode)
    }

    // 手机号格式错误
    @Test
    fun onUserPhoneIllegal() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("123")
        viewModel.onUserCodeChanged("123456")

        viewModel.onLoginClicked()

        assertEquals("手机号格式不正确", viewModel.uiState.value.userPhoneErrorMessage)
    }

    // 验证码为空
    @Test
    fun onEmptyUserCode() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("12345678900")

        viewModel.onLoginClicked()

        assertEquals("验证码不能为空", viewModel.uiState.value.userCodeErrorMessage)
    }

    // 登录成功
    @Test
    fun onLoginSuccess() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("12345678900")
        viewModel.onUserCodeChanged("123456")

        viewModel.onLoginClicked()

        assertTrue(viewModel.uiState.value.loginSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // 登录失败
    @Test
    fun onLoginFail() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("12345678900")
        viewModel.onUserCodeChanged("123456")

        viewModel.onLoginClicked()

        fakeAuthRepository.loginByPhoneResult = Result.failure(Exception("登录失败"))

        assertEquals("登录失败", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // 重置导航
    @Test
    fun onClearNavigation() = runTest {
        val viewModel = createViewModel()
        viewModel.onUserPhoneChanged("12345678900")
        viewModel.onUserCodeChanged("123456")
        val state = viewModel.uiState.value

        viewModel.onLoginClicked()
        assertTrue(state.loginSuccess)

        viewModel.onNavigatedToHome()

        assertFalse(state.loginSuccess)
    }
}
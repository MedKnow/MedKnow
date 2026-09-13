package com.medKnow.medknow.ui.screens.login.loginViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.local.TokenStore
import com.medKnow.medknow.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// 登录页 ViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    // 定义 LoginUiState 数据类
    data class LoginUiState (

        // 手机号
        val userPhone: String = "",
        // 验证码
        val userCode: String = "",
        // 是否处于正在加载状态
        val isLoading: Boolean = false,
        // 是否登陆成功
        val loginSuccess: Boolean = false,
        // 手机号错误信息
        val userPhoneErrorMessage: String? = null,
        // 验证码错误信息
        val userCodeErrorMessage: String? = null,
        // 全局错误信息
        val errorMessage: String? = null,
        // 是否可使用“登录”按钮
        val isLoginButtonEnabled: Boolean = true

    )

    // 定义 UI 状态
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 输入手机号
    fun onUserPhoneChanged (userPhone: String) {

        // 更新 userPhone 值
        _uiState.update {
            it.copy(
                userPhone = userPhone,
                userPhoneErrorMessage = null
            )
        }

    }

    // 输入验证码
    fun onUserCodeChanged(userCode: String) {

        // 更新 userCode 值
        _uiState.update {
            it.copy(
                userCode = userCode,
                userCodeErrorMessage = null
            )
        }
    }

    // 点击登录
    fun onLoginClicked() {

        val current = _uiState.value

        // 正在登录时忽略按钮重复点击
        if (current.isLoading) {
            return
        }

        // 参数校验
        var phoneError: String? = null
        var codeError: String? = null

        // 手机号校验
        phoneError = when {
            // 空值校验
            current.userPhone.isBlank() -> "手机号不能为空"
            // 格式校验
            current.userPhone.length != 11 -> "手机号格式不正确"
            else -> null
        }

        // 验证码校验
        codeError = when {
            // 空值校验
            current.userCode.isBlank() -> "验证码不能为空"
            else -> null
        }

        // 展示错误信息
        if (phoneError != null || codeError != null) {
            _uiState.update {
                it.copy(
                    userPhoneErrorMessage = phoneError,
                    userCodeErrorMessage = codeError
                )
            }
            return
        }

        // 通过参数校验，进入登录加载状态
        viewModelScope.launch {
            // 更新 _uiState 状态
            _uiState.update {
                it.copy(
                    isLoading = true,
                    userPhoneErrorMessage = null,
                    userCodeErrorMessage = null
                )
            }
            // 调用 AuthRepository.loginByPhone 函数，校验用户输入的手机号和验证码
            val result = authRepository.loginByPhone(current.userPhone, current.userCode)
                // 登陆成功，关闭 isLoading 并激活 loginSuccess
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true
                        )
                    }
                    tokenStore.saveToken(user.token)
                }
                // 登录失败，关闭 isLoading 并给出错误信息
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "登录失败"
                        )
                    }
                }
        }
    }

    // 辅助函数

    // 防止重复导航
    fun onNavigatedToHome() {
        _uiState.update {
            it.copy(
                loginSuccess = false
            )
        }
    }

}
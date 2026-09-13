package com.medKnow.medknow.ui.screens.signup.signupViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.AuthRepository
import com.medKnow.medknow.data.repository.UserRepository
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 注册页 ViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
): ViewModel() {

    // 定义 SignupState 数据类
    data class SignupUiState (

        // 手机号
        val userPhone: String = "",
        // 用户名
        val userName: String = "",
        // 验证码
        val userCode: String = "",
        // 是否处于加载状态
        val isLoading: Boolean = false,
        // 是否正在提交当前信息
        val isSubmitting: Boolean = false,
        // 是否成功保存
        val isSaved: Boolean = false,
        // 是否成功跳转到下一步
        val isNavigateSuccess: Boolean = false,
        // 手机号错误信息
        val userPhoneErrorMessage: String? = null,
        // 用户名错误信息
        val userNameErrorMessage: String? = null,
        // 验证码错误信息
        val userCodeErrorMessage: String? = null,
        // 全局错误信息
        val errorMessage: String? = null,
        // 是否正在获取验证码
        val isSendingCode: Boolean = false,

    )

    // 定义 UI 状态
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

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

    // 输入用户名
    fun onUserNameChanged (userName: String) {

        // 更新 userName 值
        _uiState.update {
            it.copy(
                userName = userName,
                userNameErrorMessage = null
            )
        }

    }

    // 输入验证码
    fun onUserCodeChanged (userCode: String) {

        // 更新 userCode 值
        _uiState.update {
            it.copy(
                userCode = userCode,
                userCodeErrorMessage = null
            )
        }

    }

    // 获取验证码
    fun onGetUserCode(phone: String) {

        // 防止重复请求
        if (_uiState.value.isSendingCode) {
            return
        }

        // 进入响应状态
        _uiState.update {
            it.copy(
                isSendingCode = true
            )
        }

        // 获取验证码
        viewModelScope.launch {
            try {
                authRepository.sendCodeByPhone(phone).getOrThrow()

                _uiState.update {
                    it.copy(
                        isSendingCode = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSendingCode = false
                    )
                }
            }
        }

    }

    // 进入下一步
    fun onNavigateToNextStepClicked() {

        val current = _uiState.value

        // 正在跳转时忽略按钮重复点击
        if (current.isLoading) {
            return
        }

        // 参数校验
        var phoneError: String? = null
        var codeError: String? = null
        var nameError: String? = null

        // 手机号校验
        phoneError = when {

            // 空值校验
            current.userPhone.isBlank() -> "手机号不能为空"
            // 格式校验
            current.userPhone.length != 11 -> "手机号格式不正确"
            // 纯数字校验
            current.userPhone.any { !it.isDigit() } -> "手机号只能包含数字"
            else -> null

        }

        // 验证码校验
        codeError = when {
            // 空值校验
            current.userCode.isBlank() -> "验证码不能为空"
            else -> null
        }

        // 用户名校验
        nameError = when {

            // 空值校验
            current.userName.isBlank() -> "用户名不能为空"
            // 长度校验
            current.userName.length !in 2..20 -> "用户名长度需在2到20之间"

            else -> null

        }

        // 展示错误信息
        if (phoneError != null || codeError != null || nameError != null) {
            _uiState.update {
                it.copy(
                    userPhoneErrorMessage = phoneError,
                    userCodeErrorMessage = codeError,
                    userNameErrorMessage = nameError
                )
            }
            return
        }

        // 通过校验，进入跳转加载状态
        viewModelScope.launch {

            // 更新 _uiState 状态
            _uiState.update {
                it.copy(
                    isLoading = true,
                    userNameErrorMessage = null,
                    userPhoneErrorMessage = null,
                    userCodeErrorMessage = null
                )
            }

            // 调用 AuthRepository.loginByPhone 函数，校验用户输入的 userPhone 和 userCode
            authRepository.loginByPhone(current.userPhone, current.userCode)
                // 跳转成功，关闭 isLoading 并激活 isNavigateSuccess
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNavigateSuccess = true
                        )
                    }
                }
                // 跳转失败，关闭 isLoading 并给出错误信息
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "注册失败"
                        )
                    }
                }

        }
    }

    // 保存注册信息
    fun onSaveSignupMessage() {

        // 保证用户已通过验证码校验
        val currentIsNavigateSuccess = _uiState.value.isNavigateSuccess
        if (!currentIsNavigateSuccess) {
            _uiState.update {
                it.copy(
                    errorMessage = "请先完成验证"
                )
            }
            return
        }

        // 忽略重复请求
        val currentIsSubmitting = _uiState.value.isSubmitting
        val currentIsLoading = _uiState.value.isLoading
        if (currentIsSubmitting || currentIsLoading) {
            return
        }

        // 获取当前 _uiState 的注册信息
        val currentUserName = _uiState.value.userName

        // 空值校验
        if (currentUserName.isBlank()) {
            _uiState.update {
                it.copy(
                    userNameErrorMessage = "用户名不能为空"
                )
            }
            return
        }

        viewModelScope.launch {

            // 更新 _uiState 状态
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null
                )
            }

            // 保存信息
            try {
                // 获取用户其他信息
                val currentUserMessage = userRepository.getUserMessage().getOrThrow()

                // 提交用户当前信息并保存
                val currentRequest = UpdateUserMessageRequest(
                    userName = currentUserName,
                    age = currentUserMessage.age,
                    allergies = currentUserMessage.allergies,
                    chronicDiseases = currentUserMessage.chronicDiseases,
                    gender = currentUserMessage.gender,
                    occupation = currentUserMessage.occupation
                )

                userRepository.updateUserMessage(currentRequest).getOrThrow()

                // 更新 _uiState 状态
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSaved = true
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "注册信息保存失败，请稍后重试"
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
                isNavigateSuccess = false,
                isSaved = false
            )
        }
    }

}
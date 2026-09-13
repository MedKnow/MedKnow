package com.medKnow.medknow.ui.screens.userPage.userPageViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.CommonRepository
import com.medKnow.medknow.data.repository.UserRepository
import com.medKnow.medknow.model.common.userFeedback.UserFeedbackRequest
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.GetUserMessageResponse
import com.medKnow.medknow.model.userModel.putAvatar.PutAvatarRequest
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 用户信息空值
val EMPTY_USER = GetUserMessageResponse(
    userId = "",
    userName = "",
    phone = "",
    avatar = "",
    age = 0,
    gender = Gender.OTHER,
    occupation = "",
    allergies = null,
    chronicDiseases = null,
    status = "",
    createdAt = ""
)

val EMPTY_FEEDBACK = UserFeedbackRequest(
    content = "",
    contact = null,
    images = emptyList()
)

// 个人中心页 ViewModel
class UserPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository
): ViewModel() {

    // 定义 UserPageUiState 数据类
    data class UserPageUiState(

        // 是否正在加载用户信息
        val isLoadingUserMessage: Boolean = false,
        // 加载用户信息时错误信息
        val loadingUserErrorMessage: String? = null,
        // 当前用户信息
        val currentUserInformation: GetUserMessageResponse = EMPTY_USER,
        // 是否正在更新头像
        val isUpdatingAvatar: Boolean = false,
        // 更新头像时错误信息
        val updatingAvatarErrorMessage: String? = null,
        // 是否正在更新用户信息
        val isUpdatingUserInformation: Boolean = false,
        // 更新用户信息时错误信息
        val updatingUserInformationErrorMessage: String? = null,
        // 全局加载状态
        val isLoading: Boolean = false,
        // 是否正在查看详细信息
        val isGettingUserInformation: Boolean = false,
        // 查看详细信息时错误信息
        val gettingUserInformationErrorMessage: String? = null,
        // 用户当前提交的反馈内容
        val currentUserFeedback: UserFeedbackRequest = EMPTY_FEEDBACK,
        // 是否正在提交反馈
        val isSubmittingFeedback: Boolean = false,
        // 提交反馈时错误信息
        val submittingFeedbackErrorMessage: String? = null

    )

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(UserPageUiState())
    val uiState: StateFlow<UserPageUiState> = _uiState.asStateFlow()

    // 自动加载个人信息
    init {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }

        onLoadUserInformation()

        _uiState.update {
            it.copy(
                isLoading = false
            )
        }

    }

    // 加载用户当前个人信息
    fun onLoadUserInformation() {

        // 防止重复请求
        if (_uiState.value.isLoadingUserMessage) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isLoadingUserMessage = true,
                loadingUserErrorMessage = null
            )
        }

        // 加载个人信息
        viewModelScope.launch {
            try {

                // 获取个人信息
                val response = userRepository.getUserMessage().getOrThrow()
                _uiState.update {
                    it.copy(
                        isLoadingUserMessage = false,
                        loadingUserErrorMessage = null,
                        currentUserInformation = response
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingUserMessage = false,
                        loadingUserErrorMessage = e.message ?: "加载信息失败",
                        currentUserInformation = EMPTY_USER
                    )
                }
            }
        }

    }

    // 更换头像
    fun onChangeAvatar(file: File) {

        // 防止重复请求
        if (_uiState.value.isUpdatingAvatar) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isUpdatingAvatar = true,
                updatingAvatarErrorMessage = null
            )
        }

        // 更换头像
        viewModelScope.launch {
            try {

                val response = userRepository.putAvatar(PutAvatarRequest(file)).getOrThrow()

                _uiState.update {
                    it.copy(
                        isUpdatingAvatar = false,
                        updatingAvatarErrorMessage = null,
                        currentUserInformation = it.currentUserInformation.copy (
                            avatar = response.avatarUrl
                        )
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: CancellationException) {
                _uiState.update {
                    it.copy(
                        isUpdatingAvatar = false,
                        updatingAvatarErrorMessage = e.message ?: "更换头像失败"
                    )
                }
            }
        }

    }

    // 更新用户信息
    fun onUpdateUserInformation(
        userName: String?,
        age: Int?,
        gender: Gender?,
        occupation: String?,
        allergies: String?,
        chronicDiseases: String?
    ) {

        // 防止重复请求
        if (_uiState.value.isUpdatingUserInformation) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isUpdatingUserInformation = true,
                updatingUserInformationErrorMessage = null
            )
        }

        // 更新用户信息
        viewModelScope.launch {
            try {

                val targetRequest = UpdateUserMessageRequest(
                    userName = userName ?: _uiState.value.currentUserInformation.userName,
                    age = age ?: _uiState.value.currentUserInformation.age,
                    gender = gender ?: _uiState.value.currentUserInformation.gender,
                    occupation = occupation ?: _uiState.value.currentUserInformation.occupation,
                    allergies = allergies ?: _uiState.value.currentUserInformation.allergies,
                    chronicDiseases = chronicDiseases ?: _uiState.value.currentUserInformation.chronicDiseases
                    )

                userRepository.updateUserMessage(targetRequest).getOrThrow()

                _uiState.update {
                    it.copy(
                        isUpdatingUserInformation = false,
                        updatingUserInformationErrorMessage = null,
                        currentUserInformation = it.currentUserInformation.copy(
                            userName = userName ?: _uiState.value.currentUserInformation.userName,
                            age = age ?: _uiState.value.currentUserInformation.age,
                            gender = gender ?: _uiState.value.currentUserInformation.gender,
                            occupation = occupation ?: _uiState.value.currentUserInformation.occupation,
                            allergies = allergies ?: _uiState.value.currentUserInformation.allergies,
                            chronicDiseases = chronicDiseases ?: _uiState.value.currentUserInformation.chronicDiseases
                        )
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingUserInformation = false,
                        updatingUserInformationErrorMessage = e.message ?: "更新信息失败"
                    )
                }
            }
        }

    }

    // 查看个人详细信息
    fun onGetUserInformation() {

        // 防止重复请求
        if(_uiState.value.isGettingUserInformation) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isGettingUserInformation = true,
                gettingUserInformationErrorMessage = null
            )
        }

        // 展示详细信息
        viewModelScope.launch {
            try {

                val response = userRepository.getUserMessage().getOrThrow()

                _uiState.update {
                    it.copy(
                        isGettingUserInformation = false,
                        currentUserInformation = response
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGettingUserInformation = false,
                        gettingUserInformationErrorMessage = e.message ?: "查看信息失败"
                    )
                }
            }
        }

    }

    // 用户提交反馈
    fun onSubmittingFeedback(
        content: String,
        contact: String?,
        images: List<String>?
    ) {

        // 防止重复请求
        if (_uiState.value.isSubmittingFeedback) {
            return
        }

        // 进入加载状态
        _uiState.update {
            it.copy(
                isSubmittingFeedback = true,
                submittingFeedbackErrorMessage = null,
                currentUserFeedback = it.currentUserFeedback.copy(
                    content = content,
                    contact = contact,
                    images = images
                )
            )
        }

        // 提交反馈
        viewModelScope.launch {
            try {

                val targetRequest = UserFeedbackRequest(
                    content = content,
                    contact = contact,
                    images = images
                )

                commonRepository.userFeedback(targetRequest).getOrThrow()

                _uiState.update {
                    it.copy(
                        isSubmittingFeedback = false,
                        submittingFeedbackErrorMessage = null,
                        currentUserFeedback = EMPTY_FEEDBACK
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmittingFeedback = false,
                        submittingFeedbackErrorMessage = e.message ?: "提交反馈失败，请稍后再试"
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
                isLoading = false
            )
        }
    }

}
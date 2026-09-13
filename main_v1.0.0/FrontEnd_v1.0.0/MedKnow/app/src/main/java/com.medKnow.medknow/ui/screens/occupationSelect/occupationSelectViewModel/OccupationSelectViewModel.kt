package com.medKnow.medknow.ui.screens.occupationSelect.occupationSelectViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.UserRepository
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 职业选择页 ViewModel
class OccupationSelectViewModel @Inject constructor( private val userRepository: UserRepository): ViewModel() {

    // 定义该页面 UI 状态
    data class OccupationSelectUiState (

        // 职业选项
        val occupation: List<String> = listOf(
            "医生", "学生", "老师", "码农", "工人",
            "律师", "骑手", "退休", "警察", "其他"
        ),
        // 选择后的职业
        val selectedOccupation: String = "",
        // 是否已提交职业信息以保存
        val isSubmitting: Boolean = false,
        // 是否保存成功
        val isSaved: Boolean = false,
        // 全局错误信息
        val errorMessage: String? = null

    )

    // 定义当前状态
    private val _uiState = MutableStateFlow(OccupationSelectUiState())
    val uiState: StateFlow<OccupationSelectUiState> = _uiState.asStateFlow()

    // 用户选择某个职业
    fun onOccupationSelected(occupation: String) {

        // 获取用户当前 occupation
        val currentOccupation = _uiState.value.selectedOccupation

        // 更新 selectedOccupation 值
        _uiState.update {
            it.copy(
                selectedOccupation = if (currentOccupation == occupation) {
                    ""
                } else {
                    occupation
                }
            )
        }

    }

    // 确认并保存到用户信息
    fun saveOccupation() {

        // 职业为必选项
        if (_uiState.value.selectedOccupation.isBlank()) {
            _uiState.update {
                it.copy(
                    errorMessage = "请选择一个职业"
                )
            }
            return
        }

        // 防止重复请求
        val currentIsSubmitting = _uiState.value.isSubmitting
        if (currentIsSubmitting) {
            return
        }

        val occupation = _uiState.value.selectedOccupation

        // 更新 _uiState 状态
        _uiState.update {
            it.copy(
                isSubmitting = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {

            // 保存职业信息
            try {

                // 获取当前用户的其他信息
                val currentUserMessage = userRepository.getUserMessage().getOrThrow()

                // 更改用户当前职业信息
                val currentUpdateUserMessageRequest = UpdateUserMessageRequest(
                    userName = currentUserMessage.userName,
                    age = currentUserMessage.age,
                    allergies = currentUserMessage.allergies,
                    chronicDiseases = currentUserMessage.chronicDiseases,
                    gender = currentUserMessage.gender,
                    occupation = occupation,
                )

                // 提交请求
                userRepository.updateUserMessage(currentUpdateUserMessageRequest).getOrThrow()

                // 更新 _uiState 状态
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        isSaved = true
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.message ?: "职业保存失败"
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
                isSaved = false
            )
        }
    }

}
package com.medKnow.medknow.ui.screens.genderSelect.genderSelectViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medKnow.medknow.data.repository.UserRepository
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// 性别选择页 ViewModel
class GenderSelectViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    // 定义 GenderSelectUiState 数据类
    data class GenderSelectUiState(

        // 性别选项
        val gender: List<Gender> = listOf(
            Gender.MALE, Gender.FEMALE, Gender.OTHER
        ),
        // 选择后的性别
        val selectedGender: Gender = Gender.OTHER,
        // 是否已递交以供保存
        val isSubmitting: Boolean = false,
        // 是否保存成功
        val isSaved: Boolean = false,
        // 全局错误信息
        val errorMessage: String? = null

    )

    // 获取当前 UI 状态
    private val _uiState = MutableStateFlow(GenderSelectUiState())
    val uiState: StateFlow<GenderSelectUiState> = _uiState.asStateFlow()

    // 用户选择某个性别
    fun onGenderSelected(selectedGender: Gender) {

        // 获取用户当前性别信息
        val currentUserGender = _uiState.value.selectedGender

        // 更新 _uiState.gender 值
        _uiState.update {
            it.copy(
                selectedGender = if (currentUserGender == selectedGender) {
                    Gender.OTHER
                } else {
                    selectedGender
                }
            )
        }

    }

    // 确认并保存性别信息
    fun saveGender() {

        // 性别为必选项
        if (_uiState.value.selectedGender == Gender.OTHER ) {
            _uiState.update {
                it.copy(
                    errorMessage = "请选择一种性别"
                )
            }
        }

        // 防止重复请求
        if (_uiState.value.isSubmitting) {
            return
        }

        // 更新 _uiState 状态进入保存阶段
        _uiState.update {
            it.copy(
                isSubmitting = true,
                errorMessage = null
            )
        }

        // 保存阶段
        viewModelScope.launch {

            // 保存性别信息
            try {

                // 获取当前用户的其他信息
                val currentUserMessage = userRepository.getUserMessage().getOrThrow()

                // 更改用户的性别信息
                val currentRequest = UpdateUserMessageRequest(
                    userName = currentUserMessage.userName,
                    age = currentUserMessage.age,
                    gender = _uiState.value.selectedGender,
                    allergies = currentUserMessage.allergies,
                    chronicDiseases = currentUserMessage.chronicDiseases,
                    occupation = currentUserMessage.occupation
                )

                // 提交保存
                userRepository.updateUserMessage(currentRequest)

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
                        errorMessage = e.message ?: "性别保存失败"
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
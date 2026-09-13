package com.medKnow.medknow.ui.screens.ageSelect.ageSelectViewModel

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

// 年龄段选择页 ViewModel
class AgeSelectViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {

    // 定义 AgeSelectUiState 数据类
    data class AgeSelectUiState (

        // 年龄段选择项
        val age: List<Int> = listOf(0, 18, 60),
        // 用户当前选择的年龄段
        val selectedAge: Int = -1,
        // 是否已提交以供保存
        val isSubmitting: Boolean = false,
        // 是否已成功保存
        val isSaved: Boolean = false,
        // 全局错误信息
        val errorMessage: String? = null

    )

    // 定义当前 UI 状态
    private val _uiState = MutableStateFlow(AgeSelectUiState())
    val uiState: StateFlow<AgeSelectUiState> = _uiState.asStateFlow()

    // 用户选择某个年龄段
    fun onAgeSelected(age: Int) {

        // 获取用户当前年龄段信息
        val currentAge = _uiState.value.selectedAge

        // 更新 _uiState.age 值
        _uiState.update {
            it.copy(
                selectedAge = if (currentAge == age) {
                    -1
                } else {
                    age
                }
            )
        }

    }

    // 确认并保存信息
    fun saveAge() {

        // 年龄段为必选项
        if (_uiState.value.selectedAge == -1) {
            _uiState.update {
                it.copy(
                    errorMessage = "请选择一种年龄段"
                )
            }
            return
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

            // 保存年龄段信息
            try {

                // 获取当前用户其他信息
                val currentUserMessage = userRepository.getUserMessage().getOrThrow()

                // 更改用户年龄段信息
                val currentRequest = UpdateUserMessageRequest(
                    userName = currentUserMessage.userName,
                    age = _uiState.value.selectedAge,
                    allergies = currentUserMessage.allergies,
                    gender = currentUserMessage.gender,
                    occupation = currentUserMessage.occupation,
                    chronicDiseases = currentUserMessage.chronicDiseases
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
                        errorMessage = e.message ?: "年龄段保存失败"
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
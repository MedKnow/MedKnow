package com.medKnow.medknow.ui.screens.genderSelect

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.ui.screens.genderSelect.genderSelectViewModel.GenderSelectViewModel
import com.medKnow.medknow.ui.theme.medKnowError
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard

// 性别选择页
@Composable
fun GenderSelectScreen(
    viewModel: GenderSelectViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOccupationSelect: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    GenderSelectContent(
        uiState = uiState,
        onGenderSelected = viewModel::onGenderSelected,
        saveGender = viewModel::saveGender,
        onNavigateToOccupationSelect = onNavigateToOccupationSelect
    )

}

// UI 布局
@Composable
fun GenderSelectContent(
    uiState: GenderSelectViewModel.GenderSelectUiState,
    onGenderSelected: (Gender) -> Unit,
    saveGender: () -> Unit,
    onNavigateToOccupationSelect : () -> Unit
) {

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 页面介绍
        Text(

            text = "完善个人信息",
            style = TextStyle(
                brush = Brush.verticalGradient(
                    colors = listOf(medKnowPrimary, medKnowStandard)
                )
            ),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(start = 24.dp, top = 60.dp)
                .fillMaxWidth()

        )

        // 页面提示
        Text(
            text = "请选择您的性别",
            color = medKnowPrimary,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // 性别选择栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            // “男性”选择框
            GenderItem(
                gender = Gender.MALE,
                isSelected = uiState.selectedGender == Gender.MALE,
                onGenderSelected = onGenderSelected,
                genderLogo = R.drawable.male_logo
            )

            Spacer(modifier = Modifier.width(40.dp))

            // “女性”选择框
            GenderItem(
                gender = Gender.FEMALE,
                isSelected = uiState.selectedGender == Gender.FEMALE,
                onGenderSelected = onGenderSelected,
                genderLogo = R.drawable.female_logo
            )

        }

        Spacer(modifier = Modifier.height(250.dp))

        // 全局错误信息展示
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = medKnowError
            )
        }

        // 下一步跳转
        Button (
            onClick = {
                saveGender()
                onNavigateToOccupationSelect()
            },
            enabled = true,
            colors = ButtonColors(
                containerColor = medKnowStandard,
                contentColor = medKnowPrimary,
                disabledContainerColor = medKnowSecondary,
                disabledContentColor = medKnowSecondary
            ),
            modifier = Modifier
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(24.dp),
                    color = medKnowPrimary
                )
                .padding(start = 10.dp, end = 10.dp)
        ) {
            if (uiState.isSaved) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = medKnowPrimary
                )
            } else {
                Text (
                    text = "下一步",
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(65.dp))

        // 产品名称
        Text(
            text = "药知道",
            color = medKnowPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // 产品简介
        Text(
            text = "值得您信赖的健康助手",
            color = medKnowSecondary,
            fontSize = 15.sp
        )
    }
}

// 性别选择框
@Composable
fun GenderItem(
    gender: Gender,
    isSelected: Boolean,
    onGenderSelected: (Gender) -> Unit,
    @DrawableRes genderLogo: Int
) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = medKnowStandard)
            .clickable { onGenderSelected(gender) }
            .border(
                width = 1.dp,
                color = if(isSelected) {
                    medKnowPrimary
                } else {
                    medKnowSecondary
                },
                shape = RoundedCornerShape(12.dp),
            )
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 性别对应 logo
        Image(
            painter = painterResource(id = genderLogo),
            colorFilter = ColorFilter.tint(
                if (isSelected) {
                    medKnowPrimary
                } else {
                    medKnowSecondary
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 性别名称
        Text(
            text = if (gender == Gender.MALE) {
                "男性"
            } else {
                "女性"
            },
            color = if (isSelected) {
                medKnowPrimary
            } else {
                medKnowSecondary
            },
            fontSize = 15.sp
        )

    }

}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun GenderSelectScreenPreview() {
    GenderSelectContent(
        uiState = GenderSelectViewModel.GenderSelectUiState(
            errorMessage = "请选择一种性别",
            selectedGender = Gender.FEMALE
        ),
        onGenderSelected = {},
        saveGender = {},
        onNavigateToOccupationSelect = {}
    )
}
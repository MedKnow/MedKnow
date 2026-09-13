package com.medKnow.medknow.ui.screens.occupationSelect

import androidx.annotation.DrawableRes
import com.medKnow.medknow.R
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.ui.screens.occupationSelect.occupationSelectViewModel.OccupationSelectViewModel
import com.medKnow.medknow.ui.theme.medKnowError
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowStandard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.medKnow.medknow.ui.theme.medKnowSecondary

// 职业选择页
@Composable
fun OccupationSelectScreen(
    viewModel: OccupationSelectViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToFirstPage: () -> Unit
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

    OccupationSelectContent(
        uiState = uiState,
        onOccupationSelected = viewModel::onOccupationSelected,
        saveOccupation = viewModel::saveOccupation,
        onNavigateToFirstPage = onNavigateToFirstPage
    )

}

// UI 布局
@Composable
fun OccupationSelectContent(
    uiState: OccupationSelectViewModel.OccupationSelectUiState,
    onOccupationSelected: (String) -> Unit,
    saveOccupation: () -> Unit,
    onNavigateToFirstPage: () -> Unit
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
            text = "请选择您的职业",
            color = medKnowPrimary,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        // 第一行职业选择项
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            // “学生”职业栏
            OccupationItem(
                occupation = "学生",
                isSelected = uiState.selectedOccupation == "学生",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.student_logo,
                modifier = Modifier.weight(1f)
            )

            // “老师”职业栏
            OccupationItem(
                occupation = "老师",
                isSelected = uiState.selectedOccupation == "老师",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.teacher_logo,
                modifier = Modifier.weight(1f)
            )

            // “码农”职业栏
            OccupationItem(
                occupation = "码农",
                isSelected = uiState.selectedOccupation == "码农",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.coder_logo,
                modifier = Modifier.weight(1f)
            )

            // "工人"职业栏
            OccupationItem(
                occupation = "工人",
                isSelected = uiState.selectedOccupation == "工人",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.worker_logo,
                modifier = Modifier.weight(1f)
            )

            // "医生“职业栏
            OccupationItem(
                occupation = "医生",
                isSelected = uiState.selectedOccupation == "医生",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.doctor_logo,
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        // 第二行职业选择项
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            // “警察”职业栏
            OccupationItem(
                occupation = "警察",
                isSelected = uiState.selectedOccupation == "警察",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.police_logo,
                modifier = Modifier.weight(1f)
            )

            // “律师”职业栏
            OccupationItem(
                occupation = "律师",
                isSelected = uiState.selectedOccupation == "律师",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.lawyer_logo,
                modifier = Modifier.weight(1f)
            )

            // “退休”职业栏
            OccupationItem(
                occupation = "退休",
                isSelected = uiState.selectedOccupation == "退休",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.retire_logo,
                modifier = Modifier.weight(1f)
            )

            // “骑手”职业栏
            OccupationItem(
                occupation = "骑手",
                isSelected = uiState.selectedOccupation == "骑手",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.rider_logo,
                modifier = Modifier.weight(1f)
            )

            // “其他”职业栏
            OccupationItem(
                occupation = "其他",
                isSelected = uiState.selectedOccupation == "其他",
                onOccupationSelected = onOccupationSelected,
                occupationLogo = R.drawable.other_logo,
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(160.dp))

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
                saveOccupation()
                onNavigateToFirstPage()
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
                    text = "进入首页",
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

// 职业选择框
@Composable
fun OccupationItem(
    occupation: String,
    isSelected: Boolean,
    onOccupationSelected: (String) -> Unit,
    @DrawableRes occupationLogo: Int,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = medKnowStandard)
            .clickable { onOccupationSelected(occupation) }
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

        // 职业对应 logo
        Image(
            painter = painterResource(id = occupationLogo),
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

        // 职业名称
        Text(
            text = occupation,
            color = if (isSelected) {
                medKnowPrimary
            } else {
                medKnowSecondary
            },
            fontSize = 15.sp
        )

    }

}

// UI 预览函数
@Preview(showBackground = true)
@Composable
fun OccupationSelectScreenPreview() {
    OccupationSelectContent(
        uiState = OccupationSelectViewModel.OccupationSelectUiState(
            selectedOccupation = "医生",
            errorMessage = "请至少选择一个职业"
        ),
        onOccupationSelected = {},
        saveOccupation = {},
        onNavigateToFirstPage = {}
    )
}
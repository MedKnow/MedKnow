package com.medKnow.medknow.ui.screens.userPage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medKnow.medknow.R
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.UserPageViewModel
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 查看详细个人信息页
@Composable
fun CheckUserInformationScreen(
    viewModel: UserPageViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToUserPage: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    CheckUserInformationContent(
        uiState = uiState,
        onNavigateToUserPage = onNavigateToUserPage
    )

}

// UI 布局
@Composable
fun CheckUserInformationContent(
    uiState: UserPageViewModel.UserPageUiState,
    onNavigateToUserPage: () -> Unit
) {
    // 父容器
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentAlignment = Alignment.TopCenter
            ) {

                // 头像背景绘制
                Canvas(modifier = Modifier.fillMaxSize()) {
                    rotate(degrees = 180f) {
                        drawArc(
                            color = medKnowPrimary,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = true,
                            topLeft = Offset(-size.width / 2, (size.height / 2) + 150),
                            size = Size(size.width * 2, size.height)
                        )
                    }
                }

                // 返回键
                Image(
                    painter = painterResource(id = R.drawable.articlereturn_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(medKnowStandard),
                    modifier = Modifier
                        .padding(top = 60.dp, start = 10.dp, end = 300.dp)
                        .size(35.dp)
                        .clickable { onNavigateToUserPage() }
                )

                // 头像
                AsyncImage(
                    model = uiState.currentUserInformation.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 120.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(medKnowStandard)
                )

            }
        }

        // 账号编号
        item {
            CheckUserInformationStringItem(
                title = "用户编号",
                targetValue = uiState.currentUserInformation.userId
            )
        }

        // 用户名
        item {
            CheckUserInformationStringItem(
                title = "用户名",
                targetValue = uiState.currentUserInformation.userName
            )
        }

        // 手机号
        item {
            CheckUserInformationStringItem(
                title = "手机号",
                targetValue = uiState.currentUserInformation.phone.maskPhone()
            )
        }

        // 年龄
        item {
            CheckUserInformationStringItem(
                title = "年龄",
                targetValue = "${uiState.currentUserInformation.age}",
            )
        }

        // 性别
        item {
            CheckUserInformationStringItem(
                title = "性别",
                targetValue = when(uiState.currentUserInformation.gender) {
                    Gender.MALE -> "男"
                    Gender.FEMALE -> "女"
                    else -> "其他"
                }
            )
        }

        // 职业
        item {
            CheckUserInformationStringItem(
                title = "职业",
                targetValue = uiState.currentUserInformation.occupation,
            )
        }

        // 过敏史
        item {
            CheckUserInformationStringItem(
                title = "过敏史",
                targetValue = uiState.currentUserInformation.allergies ?: "暂无过敏史"
            )
        }

        // 慢性病史
        item {
            CheckUserInformationStringItem(
                title = "慢性病史",
                targetValue = uiState.currentUserInformation.chronicDiseases ?: "暂无慢性病史",
            )
        }

        // 账户状态
        item {
            CheckUserInformationStringItem(
                title = "账户状态",
                targetValue = uiState.currentUserInformation.status
            )
        }

        // 注册时间
        item {
            CheckUserInformationStringItem(
                title = "注册时间",
                targetValue = uiState.currentUserInformation.createdAt
            )
        }

    }

}

// 查看个人信息操作栏
@Composable
fun CheckUserInformationStringItem(
    title: String,
    targetValue: String
) {

    Spacer(modifier = Modifier.height(15.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-200).dp)
    ) {
        // 标题
        Text(
            text = title,
            color = medKnowPrimary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp, bottom = 5.dp)
        )
        OutlinedTextField(
            value = targetValue,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = {
                Text(
                    text = targetValue,
                    color = medKnowSecondary,
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard,
                focusedIndicatorColor = medKnowPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowSecondary,
                disabledTextColor = medKnowSecondary,
                disabledIndicatorColor = medKnowSecondary,
                disabledContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

// 手机号脱敏展示
fun String.maskPhone(): String {
    return if (length == 11) {
        replaceRange(4, 8, "****")
    } else {
        this
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun CheckUserInformationScreenPreview() {
    CheckUserInformationContent(
        uiState = UserPageViewModel.UserPageUiState(),
        onNavigateToUserPage = {}
    )
}
package com.medKnow.medknow.ui.screens.userPage

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medKnow.medknow.R
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.GetUserMessageResponse
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.UserPageViewModel
import com.medKnow.medknow.ui.theme.medKnowNavigationBackground
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 个人中心页
@Composable
fun UserPageScreen(
    viewModel: UserPageViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCheckUserInformation: () -> Unit,
    onNavigateToUpdateUserInformation: () -> Unit,
    onNavigateToUserFeedback: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit
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

    UserPageContent(
        uiState = uiState,
        onNavigateToCheckUserInformation = onNavigateToCheckUserInformation,
        onNavigateToUpdateUserInformation = onNavigateToUpdateUserInformation,
        onNavigateToUserFeedback = onNavigateToUserFeedback,
        onNavigateToFirstPage = onNavigateToFirstPage,
        onNavigateToHealthArticle = onNavigateToHealthArticle,
        onNavigateToVisitNavigation = onNavigateToVisitNavigation,
        onNavigateToMedicationPlan = onNavigateToMedicationPlan
    )

}

// UI 布局
@Composable
fun UserPageContent(
    uiState: UserPageViewModel.UserPageUiState,
    onNavigateToCheckUserInformation: () -> Unit,
    onNavigateToUpdateUserInformation: () -> Unit,
    onNavigateToUserFeedback: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit
) {

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // 主要信息框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp, start = 10.dp, end = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(medKnowPrimary)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(20.dp),
                        color = medKnowPrimary
                    )
                    .padding(end = 10.dp, top = 15.dp, bottom = 15.dp, start = 30.dp)
            ) {

                // 主要信息栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // 头像展示
                    AsyncImage(
                        model = uiState.currentUserInformation.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(medKnowStandard)
                    )
                    // 用户 Id 和用户名
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 40.dp)
                    ) {
                        // 用户 Id
                        Text(
                            text = "用户编号：" + uiState.currentUserInformation.userId,
                            color = medKnowStandard,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        // 用户名
                        Text(
                            text = uiState.currentUserInformation.userName,
                            color = medKnowStandard,
                            fontSize = 20.sp
                        )
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            // 药箱
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageOperationItem(
                    operationName = "药箱",
                    operationLogo = R.drawable.cabinet_logo
                )
            }

            // 收藏本
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageOperationItem(
                    operationName = "收藏本",
                    operationLogo = R.drawable.collectbook_logo
                )
            }

            // 系统消息
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageOperationItem(
                    operationName = "系统消息",
                    operationLogo = R.drawable.systemmessage_logo
                )
            }

        }

        Spacer(modifier = Modifier.height(50.dp))

        // 更多内容
        Text(
            text = "更多内容",
            color = medKnowPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 更改个人信息
        OperationItem(
            operation = "更改个人信息",
            onClick = { onNavigateToUpdateUserInformation() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 查看个人详细信息
        OperationItem(
            operation = "查看个人详细信息",
            onClick = { onNavigateToCheckUserInformation() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 提交用户反馈
        OperationItem(
            operation = "用户反馈",
            onClick = { onNavigateToUserFeedback() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 关于团队
        OperationItem(
            operation = "关于团队",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(212.dp))

        // 底部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(medKnowNavigationBackground)
                .clip(RectangleShape)
                .padding(vertical = 5.dp, horizontal = 5.dp)
        ) {

            // 首页
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable{ onNavigateToFirstPage() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 首页 logo
                Image(
                    painter = painterResource(id = R.drawable.firstpage_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
                // “首页”
                Text(
                    text = "首页",
                    color = medKnowStandardBlack,
                    fontSize = 12.sp
                )

            }

            // 科普
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable{ onNavigateToHealthArticle() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 科普 logo
                Image(
                    painter = painterResource(id = R.drawable.article_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )

                // “科普”
                Text(
                    text = "科普",
                    color = medKnowStandardBlack,
                    fontSize = 12.sp
                )

            }

            // 日程表
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable{ onNavigateToMedicationPlan() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 日程表 logo
                Image(
                    painter = painterResource(id = R.drawable.plan_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )

                // “日程表”
                Text(
                    text = "日程表",
                    color = medKnowStandardBlack,
                    fontSize = 12.sp
                )

            }

            // 紧急就诊
            Column (
                modifier = Modifier
                    .weight(1f)
                    .clickable{ onNavigateToVisitNavigation() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 紧急就诊 logo
                Image(
                    painter = painterResource(id = R.drawable.visitnavigation_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )

                // “紧急就诊”
                Text(
                    text = "紧急就诊",
                    color = medKnowStandardBlack,
                    fontSize = 12.sp
                )

            }

            // 个人中心
            Column (
                modifier = Modifier
                    .weight(1f)
                    .clickable{},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 个人中心 logo
                Image(
                    painter = painterResource(id = R.drawable.userpage_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(medKnowPrimary),
                    modifier = Modifier
                        .size(30.dp)
                )

                // “个人中心”
                Text(
                    text = "个人中心",
                    color = medKnowPrimary,
                    fontSize = 12.sp
                )

            }

        }
    }

}

// 图标操作栏
@Composable
fun ImageOperationItem(
    operationName: String,
    @DrawableRes operationLogo: Int
) {
    // logo
    Image(
        painter = painterResource(operationLogo),
        colorFilter = ColorFilter.tint(medKnowPrimary),
        contentDescription = null,
        modifier = Modifier
            .size(30.dp)
    )
    // 操作名称
    Text(
        text = operationName,
        color = medKnowStandardBlack,
        fontSize = 12.sp
    )

}

// 更多内容操作栏
@Composable
fun OperationItem(
    operation: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 10.dp, end = 15.dp, top = 5.dp, bottom = 5.dp)
    ) {
        // 操作名称
        Text(
            text = operation,
            color = medKnowStandardBlack,
            fontSize = 15.sp,
            modifier = Modifier
                .weight(1f)
                .padding(top = 5.dp, start = 10.dp)
        )
        // 进入键
        Image(
            painter = painterResource(id = R.drawable.enter_logo),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
        )
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun UserPageScreenPreview() {
    UserPageContent(
        uiState = UserPageViewModel.UserPageUiState(
            currentUserInformation = GetUserMessageResponse(
                userId = "1001",
                userName = "周易权",
                phone = "13800000000",
                avatar = "http://localhost:8090/default.png",
                age = 30,
                gender = Gender.MALE,
                occupation = "清洁工",
                allergies = "花粉过敏",
                chronicDiseases = "糖尿病",
                status = "NORMAL",
                createdAt = "2026-08-07 12:13:50"
            )
        ),
        onNavigateToCheckUserInformation = {},
        onNavigateToUpdateUserInformation = {},
        onNavigateToUserFeedback = {},
        onNavigateToFirstPage = {},
        onNavigateToHealthArticle = {},
        onNavigateToVisitNavigation = {},
        onNavigateToMedicationPlan = {}
    )
}
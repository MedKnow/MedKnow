package com.medKnow.medknow.ui.screens.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.ui.screens.login.loginViewModel.LoginViewModel
import com.medKnow.medknow.R
import com.medKnow.medknow.ui.theme.medKnowError
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard

// 登录页
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToFirstPage: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onUserNameChanged = viewModel::onUserPhoneChanged,
        onUserCodeChanged = viewModel::onUserCodeChanged,
        onLoginClicked = viewModel::onLoginClicked,
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToFirstPage = onNavigateToFirstPage
    )

}

// UI 布局
@Composable
fun LoginScreenContent(
    uiState: LoginViewModel.LoginUiState,
    onUserNameChanged: (String) -> Unit,
    onUserCodeChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToFirstPage: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = medKnowPrimary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .offset(y = (-20).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // logo 背景绘制
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    brush = Brush.verticalGradient(
                        colors = listOf(medKnowPrimary, medKnowStandard)
                    ),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(-size.width / 2, -size.height * 2 + 100f),
                    size = Size(size.width * 2, size.height * 3)
                )
            }
        }

        // logo 图案
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(125.dp)
                .offset(x = 0.dp, y = (-94).dp)
        )

        // 手机号与验证码输入框
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(medKnowStandard)
                .border(
                    width =1.dp,
                    color = medKnowPrimary,
                    shape = RoundedCornerShape(20.dp),
                )
        ){

            Spacer(modifier = Modifier.height(10.dp))

            // 手机号输入框
            OutlinedTextField(
                value = uiState.userPhone,
                onValueChange = onUserNameChanged,
                placeholder = { Text("请输入手机号") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                isError = uiState.userPhoneErrorMessage != null,
                supportingText = {
                    if (uiState.userPhoneErrorMessage != null) {
                        Text(text = uiState.userPhoneErrorMessage)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = medKnowPrimary,
                    unfocusedTextColor = medKnowPrimary,
                    focusedIndicatorColor = medKnowPrimary,
                    unfocusedIndicatorColor = medKnowSecondary,
                    focusedContainerColor = medKnowStandard,
                    unfocusedContainerColor = medKnowStandard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 验证码输入框
                OutlinedTextField(
                    value = uiState.userCode,
                    onValueChange = onUserCodeChanged,
                    placeholder = { Text("请输入验证码") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    isError = uiState.userCodeErrorMessage != null,
                    supportingText = {
                        if (uiState.userCodeErrorMessage != null) {
                            Text(text = uiState.userCodeErrorMessage)
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = medKnowPrimary,
                        unfocusedTextColor = medKnowPrimary,
                        focusedIndicatorColor = medKnowPrimary,
                        unfocusedIndicatorColor = medKnowSecondary,
                        focusedContainerColor = medKnowStandard,
                        unfocusedContainerColor = medKnowStandard
                    ),
                    modifier = Modifier
                        .weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onLoginClicked,
                    enabled = true,
                    colors = ButtonColors(
                        containerColor = medKnowPrimary,
                        contentColor = medKnowStandard,
                        disabledContainerColor = medKnowSecondary,
                        disabledContentColor = medKnowStandard
                    ),
                    modifier = Modifier
                        .height(45.dp)
                ) {
                    Text("获取验证码")
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                // 注册页跳转
                Text(
                    text = "还没有账号？",
                    fontSize = 12.sp,
                    color = medKnowSecondary
                )

                Text(
                    text = "点此注册",
                    color = medKnowPrimary,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable {
                            onLoginClicked()
                            onNavigateToSignUp()
                        }
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 全局错误信息显示
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = medKnowError
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 登录按钮
        Button(
            onClick = {
                onLoginClicked()
                onNavigateToFirstPage()
            },
            enabled = true,
            colors = ButtonColors(
                containerColor = medKnowPrimary,
                contentColor = medKnowStandard,
                disabledContentColor = medKnowStandard,
                disabledContainerColor = medKnowSecondary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = medKnowPrimary
                )
            } else {
                Text("登录")
            }
        }

        Spacer(modifier = Modifier.height(70.dp))

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

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme{
        LoginScreenContent(
            uiState = LoginViewModel.LoginUiState(
                userPhoneErrorMessage = "手机号不能为空",
                userCodeErrorMessage = "验证码不能为空",
                errorMessage = "登录失败"
            ),
            onUserNameChanged = {},
            onUserCodeChanged = {},
            onLoginClicked = {},
            onNavigateToSignUp = {},
            onNavigateToFirstPage = {}
        )
    }
}
package com.medKnow.medknow.ui.screens.userPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.common.userFeedback.UserFeedbackRequest
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.UserPageViewModel
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 用户反馈页
@Composable
fun UserFeedbackScreen(
    viewModel: UserPageViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToUserPage: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.isLoading){
        if(uiState.isLoading) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    UserFeedbackContent(
        uiState = uiState,
        onSubmittingFeedback = viewModel::onSubmittingFeedback,
        onNavigateToUserPage = onNavigateToUserPage
    )

}

// UI 布局
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFeedbackContent(
    uiState: UserPageViewModel.UserPageUiState,
    onSubmittingFeedback: (String, String?, List<String>?) -> Unit,
    onNavigateToUserPage: () -> Unit
) {

    // 新增图片弹窗是否开启
    var showAddImageSheet by remember { mutableStateOf(false) }
    var addImageSheetState = rememberModalBottomSheetState()

    // 用户当前所填反馈信息
    var targetUserFeedback by remember {
        mutableStateOf(
            UserFeedbackRequest(
                contact = null,
                content = "",
                images = emptyList()
            )
        )
    }

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 返回键
        Image(
            painter = painterResource(R.drawable.articlereturn_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(medKnowPrimary),
            modifier = Modifier
                .padding(start = 10.dp, top = 55.dp, end = 300.dp)
                .size(30.dp)
                .clickable { onNavigateToUserPage() }
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 标题
        Text(
            text = "用户反馈",
            color = medKnowPrimary,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(100.dp))

        //“联系方式”输入框
        // 标题
        Text(
            text = "联系方式",
            color = medKnowSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, bottom = 5.dp)
        )
        // 输入框
        OutlinedTextField(
            value = targetUserFeedback.contact ?: "",
            onValueChange = { newValue ->
                targetUserFeedback = targetUserFeedback.copy(
                    contact = newValue
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowSecondary,
                focusedIndicatorColor = medKnowPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // “反馈内容”输入框
        // 标题
        Text(
            text = "反馈内容",
            color = medKnowSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, bottom = 5.dp)
        )
        // 输入框
        OutlinedTextField(
            value = targetUserFeedback.content,
            onValueChange = { newValue ->
                targetUserFeedback = targetUserFeedback.copy(
                    content = newValue
                )
            },
            shape = RoundedCornerShape(20.dp),
            maxLines = 1,
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowSecondary,
                focusedIndicatorColor = medKnowPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // “截图”输入框
        Text(
            text = "截图",
            color = medKnowSecondary,
            textAlign = TextAlign.Start,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp, bottom = 5.dp)
                .fillMaxWidth()
        )

        // 输入框
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(20.dp),
                    color = medKnowSecondary
                )
                .padding(horizontal = 10.dp, vertical = 15.dp)
        ) {

            // 已添加图片列表
            items(targetUserFeedback.images ?: emptyList() ) { imageUrl ->
                ImageItem(url = imageUrl)

                Spacer(modifier = Modifier.height(10.dp))

            }

            // 新增键
            item {
                Button(
                    onClick = { showAddImageSheet = true },
                    enabled = true,
                    colors = ButtonColors(
                        containerColor = medKnowStandard,
                        contentColor = medKnowPrimary,
                        disabledContainerColor = medKnowStandard,
                        disabledContentColor = medKnowStandard
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                    ) {
                    Text(
                        text = "+ 新增图片",
                        color = medKnowSecondary,
                        fontSize = 15.sp
                    )
                }
            }
        }
        // 新增图片弹窗
        if (showAddImageSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddImageSheet = false },
                sheetState = addImageSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 50.dp)
                ) {
                    // 标题
                    Text(
                        text = "添加图片",
                        color = medKnowStandardBlack,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    // 操作栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 60.dp)
                    ) {
                        // 从相机获取
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {},
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PutAvatarOperationItem(
                                operationName = "相机",
                                operationLogo = R.drawable.crema_logo
                            )
                        }
                        // 从相册获取
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {},
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PutAvatarOperationItem(
                                operationName = "相册",
                                operationLogo = R.drawable.images_logo
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // 提交键
        Button(
            onClick = {
                onSubmittingFeedback(
                    targetUserFeedback.content,
                    targetUserFeedback.contact,
                    targetUserFeedback.images
                )
            },
            enabled = true,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonColors(
                disabledContentColor = medKnowStandardBlack,
                disabledContainerColor = medKnowSecondary,
                containerColor = medKnowPrimary,
                contentColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("确认提交")
        }

    }
}

// 图片信息栏
@Composable
fun ImageItem(
    url: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.dp,
                color = medKnowPrimary,
                shape = RoundedCornerShape(20.dp)
            )
            .background(medKnowPrimary)
            .padding(vertical = 15.dp, horizontal = 10.dp)
    ) {
        Text(
            text = url,
            color = medKnowStandard,
            fontSize = 15.sp
        )
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun UserFeedbackScreenPreview() {
    UserFeedbackContent(
        uiState = UserPageViewModel.UserPageUiState(),
        onSubmittingFeedback = {_, _, _ ->},
        onNavigateToUserPage = {}
    )
}
package com.medKnow.medknow.ui.screens.userPage

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medKnow.medknow.R
import com.medKnow.medknow.model.userModel.Gender
import com.medKnow.medknow.model.userModel.updateUserMessage.UpdateUserMessageRequest
import com.medKnow.medknow.ui.screens.userPage.userPageViewModel.UserPageViewModel
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack
import com.medKnow.medknow.ui.theme.medKnowThird

// 更新用户信息页
@Composable
fun UpdateUserInformationScreen(
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

    UpdateUserInformationContent(
        uiState = uiState,
        onUpdateUserInformation = viewModel::onUpdateUserInformation,
        onNavigateToUserPage = onNavigateToUserPage
    )

}

// UI 布局
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserInformationContent(
    uiState: UserPageViewModel.UserPageUiState,
    onUpdateUserInformation: (String?, Int?, Gender?, String?, String?, String?) -> Unit,
    onNavigateToUserPage: () -> Unit
) {

    // 更换头像弹窗是否开启
    var showPutAvatarSheet by remember { mutableStateOf(false) }
    var putAvatarSheetState = rememberModalBottomSheetState()

    // 性别选择弹窗是否开启
    var showGenderSelectSheet by remember { mutableStateOf(false) }
    var genderSelectSheetState = rememberModalBottomSheetState()

    // 用户将用于更改的个人信息
    var targetUserInformation by remember {
        mutableStateOf(
            UpdateUserMessageRequest(
                userName = null,
                age = null,
                gender = null,
                occupation = null,
                allergies = null,
                chronicDiseases = null,
            )
        )
    }

    // 性别字符串代替量
    var genderReplace by remember { mutableStateOf("") }

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
    ) {

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
                    .padding(top = 50.dp, start = 10.dp, end = 310.dp)
                    .size(30.dp)
                    .clickable { onNavigateToUserPage() }
            )

            // 头像
            AsyncImage(
                model = uiState.currentUserInformation.avatar,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 75.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(medKnowStandard)
            )

            // 更换头像操作
            Text(
                text = "更换头像",
                color = medKnowStandard,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(145.dp)
                    .clickable { showPutAvatarSheet = true }
            )
            // 启用弹窗
            if (showPutAvatarSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPutAvatarSheet = true },
                    sheetState = putAvatarSheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 50.dp)
                    ) {
                        // 标题
                        Text(
                            text = "更换头像",
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

        }

        // 用户名输入框
        UpdateUserInformationStringItem(
            title = "用户名",
            targetValue = targetUserInformation.userName,
            onTargetValueChange = { newValue ->
                targetUserInformation = targetUserInformation.copy(
                    userName = newValue
                )
            },
            holderTitle = "请输入用户名",
            currentValue = uiState.currentUserInformation.userName
        )

        // 年龄输入框
        UpdateUserInformationIntItem(
            title = "年龄",
            targetValue = targetUserInformation.age,
            onTargetValueChange = {newValue ->
                targetUserInformation = targetUserInformation.copy(
                    age = newValue
                )
            },
            holderTitle = "请输入年龄"
        )

        // 性别选择框
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {showGenderSelectSheet = true }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-250).dp)
            ) {
                // 标题
                Text(
                    text = "性别",
                    color = medKnowPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 15.dp, bottom = 5.dp)
                )
                OutlinedTextField(
                    value = genderReplace,
                    onValueChange = { },
                    enabled = false,
                    readOnly = true,
                    placeholder = {
                        Text(
                            text = "请选择性别",
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
                        disabledContainerColor = medKnowStandard,
                        disabledTextColor = medKnowSecondary,
                        disabledIndicatorColor = medKnowSecondary
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showGenderSelectSheet = true }
                )
            }
        }

        // 显示性别选择弹窗
        if (showGenderSelectSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGenderSelectSheet = false },
                sheetState = genderSelectSheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 50.dp)
                ) {
                    // 标题
                    Text(
                        text = "请选择性别",
                        color = medKnowPrimary,
                        fontSize = 23.sp,
                        modifier = Modifier
                            .padding(start = 10.dp, bottom = 40.dp)
                    )
                    // 性别选项
                    // “男性”选择框
                    Text(
                        text = "男",
                        color = if (targetUserInformation.gender == Gender.MALE) {
                            medKnowPrimary
                        } else {
                            medKnowSecondary
                        },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                genderReplace = "男"
                                targetUserInformation = targetUserInformation.copy(
                                    gender = Gender.MALE
                                )
                                showGenderSelectSheet = false
                            }
                    )

                    HorizontalDivider(
                        color = medKnowThird,
                        thickness = 2.dp,
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 45.dp)
                    )

                    // “女性”选择框
                    Text(
                        text = "女",
                        color = if (targetUserInformation.gender == Gender.FEMALE) {
                            medKnowPrimary
                        } else {
                            medKnowSecondary
                        },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                genderReplace = "女"
                                targetUserInformation = targetUserInformation.copy(
                                    gender = Gender.FEMALE
                                )
                                showGenderSelectSheet = false
                            }
                    )

                    HorizontalDivider(
                        color = medKnowThird,
                        thickness = 2.dp,
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 50.dp)
                    )

                    // “其他”选择框
                    Text(
                        text = "其他",
                        color = if (targetUserInformation.gender == Gender.OTHER) {
                            medKnowPrimary
                        } else {
                            medKnowSecondary
                        },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                genderReplace = "其他"
                                targetUserInformation = targetUserInformation.copy(
                                    gender = Gender.OTHER
                                )
                                showGenderSelectSheet = false
                            }
                    )

                }
            }
        }

        // 职业输入框
        UpdateUserInformationStringItem(
            title = "职业",
            targetValue = targetUserInformation.occupation,
            onTargetValueChange = { newValue ->
                targetUserInformation = targetUserInformation.copy(
                    occupation = newValue
                )
            },
            holderTitle = "请输入职业",
            currentValue = uiState.currentUserInformation.occupation
        )

        // 过敏史
        UpdateUserInformationStringItem(
            title = "过敏史",
            targetValue = targetUserInformation.allergies,
            onTargetValueChange = { newValue ->
                targetUserInformation = targetUserInformation.copy(
                    allergies = newValue
                )
            },
            holderTitle = "请输入过敏史",
            currentValue = uiState.currentUserInformation.allergies ?: ""
        )

        // 慢性病史
        UpdateUserInformationStringItem(
            title = "慢性病史",
            targetValue = targetUserInformation.chronicDiseases,
            onTargetValueChange = { newValue ->
                targetUserInformation = targetUserInformation.copy(
                    chronicDiseases = newValue
                )
            },
            holderTitle = "请输入慢性病史",
            currentValue = uiState.currentUserInformation.chronicDiseases ?: ""
        )

        // 确认键
        Button(
            onClick = {
                onUpdateUserInformation(
                    targetUserInformation.userName,
                    targetUserInformation.age,
                    targetUserInformation.gender,
                    targetUserInformation.occupation,
                    targetUserInformation.allergies,
                    targetUserInformation.chronicDiseases
                )
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-150).dp),
            colors = ButtonColors(
                containerColor = medKnowPrimary,
                contentColor = medKnowStandard,
                disabledContainerColor = medKnowSecondary,
                disabledContentColor = medKnowStandardBlack
            )
        ) {
            Text("确认更改")
        }

    }

}

// 更新个人信息文本类操作栏
@Composable
fun UpdateUserInformationStringItem(
    title: String,
    targetValue: String?,
    onTargetValueChange: (String) -> Unit,
    holderTitle: String,
    currentValue: String
) {

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-250).dp)
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
            value = targetValue ?: currentValue,
            onValueChange = { onTargetValueChange(it) },
            placeholder = {
                Text(
                    text = holderTitle,
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

                ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

// 更新个人信息数值类操作栏
@Composable
fun UpdateUserInformationIntItem(
    title: String,
    targetValue: Int?,
    onTargetValueChange: (Int) -> Unit,
    holderTitle: String
) {

    Spacer(modifier = Modifier.height(10.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-250).dp)
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
            value = targetValue?.toString() ?: "",
            onValueChange = { newValue ->
                newValue.toIntOrNull()?.let { onTargetValueChange(it) }
            },
            placeholder = {
                Text(
                    text = holderTitle,
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

                ),
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

// 更换头像操作栏
@Composable
fun PutAvatarOperationItem(
    operationName: String,
    @DrawableRes operationLogo: Int
) {
    // 操作 logo
    Image(
        painter = painterResource(operationLogo),
        contentDescription = null,
        colorFilter = ColorFilter.tint(medKnowPrimary),
        modifier = Modifier
            .size(30.dp)
    )
    // 操作名称
    Text(
        text = operationName,
        color = medKnowStandardBlack,
        fontSize = 15.sp
    )
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun UpdateUserInformationScreenPreview() {
    UpdateUserInformationContent(
        uiState = UserPageViewModel.UserPageUiState(),
        onUpdateUserInformation = {_, _, _, _, _,_ ->},
        onNavigateToUserPage = {}
    )
}
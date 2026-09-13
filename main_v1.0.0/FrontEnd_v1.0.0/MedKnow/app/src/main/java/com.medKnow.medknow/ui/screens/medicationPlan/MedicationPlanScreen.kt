package com.medKnow.medknow.ui.screens.medicationPlan

import androidx.compose.ui.graphics.Color
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.TakeMethod
import com.medKnow.medknow.model.medicationPlans.getMedicationPlanList.Plan
import com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel.MedicationPlanViewModel
import com.medKnow.medknow.ui.theme.medKnowNavigationBackground
import com.medKnow.medknow.ui.theme.medKnowPlanBlue
import com.medKnow.medknow.ui.theme.medKnowPlanGreen
import com.medKnow.medknow.ui.theme.medKnowPlanPrimary
import com.medKnow.medknow.ui.theme.medKnowPlanSecondary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack
import com.medKnow.medknow.util.LunarCalendar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 用药计划页
@Composable
fun MedicationPlanScreen(
    viewModel: MedicationPlanViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCreateMedicationPlan: () -> Unit,
    onNavigateToMedicationInformation: () -> Unit,
    onNavigateToUpdateMedicationPlan: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
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

    MedicationPlanContent(
        uiState = uiState,
        onCreateMedicationPlan = viewModel::onCreateMedicationPlan,
        onCreateDrug = viewModel::onCreateDrug,
        onMedicationPlanInformation = viewModel::onMedicationPlanInformation,
        onActivePlan = viewModel::onActivePlan,
        onStopPlan = viewModel::onStopPlan,
        onResumePlan = viewModel::onResumePlan,
        onUpdatePlanDrug = viewModel::onUpdatePlanDrug,
        onUpdateMedicationPlan = viewModel::onUpdateMedicationPlan,
        loadMore = viewModel::loadMore,
        onPlanSort = viewModel::onPlanSort,
        onComplexOperationPlanIdSave = viewModel::onComplexOperationPlanIdSave,
        onNavigateToCreateMedicationPlan = onNavigateToCreateMedicationPlan,
        onNavigateToMedicationInformation = onNavigateToMedicationInformation,
        onNavigateToUpdateMedicationPlan = onNavigateToUpdateMedicationPlan,
        onNavigateToFirstPage = onNavigateToFirstPage,
        onNavigateToHealthArticle = onNavigateToHealthArticle,
        onNavigateToVisitNavigation = onNavigateToVisitNavigation,
        onNavigateToUserPage = onNavigateToUserPage
    )

}

// UI 布局
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationPlanContent(
    uiState: MedicationPlanViewModel.MedicationPlanUiState,
    onCreateMedicationPlan: (String, String, String?, String?, List<String>) -> Unit,
    onCreateDrug: (String, String, String, String, TakeMethod, String?) -> Unit,
    onMedicationPlanInformation: (Int) -> Unit,
    onActivePlan: (Int) -> Unit,
    onStopPlan: (Int) -> Unit,
    onResumePlan: (Int) -> Unit,
    onUpdatePlanDrug: (String, String, String, String, TakeMethod, String?) -> Unit,
    onUpdateMedicationPlan: (Int, String, String, String?, String?, List<String>) -> Unit,
    loadMore: () -> Unit,
    onPlanSort: (PlanStatus?) -> Unit,
    onComplexOperationPlanIdSave: (Int) -> Unit,
    onNavigateToCreateMedicationPlan: () -> Unit,
    onNavigateToMedicationInformation: () -> Unit,
    onNavigateToUpdateMedicationPlan: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToUserPage: () -> Unit
) {

    // 用于轮换的计划栏背景颜色
    val backgroundColors = listOf(
        medKnowPlanPrimary, medKnowPlanBlue, medKnowPlanGreen, medKnowPlanSecondary
    )
    // 用于实现轮换颜色的索引
    var index = 1

    // 激活计划弹窗是否显示
    var showActiveSheet by remember { mutableStateOf(false) }
    var activeSheetState = rememberModalBottomSheetState()
    var activePlanNumber by remember { mutableStateOf("") }

    // 暂停计划弹窗是否显示
    var showStopSheet by remember { mutableStateOf(false) }
    var stopSheetState = rememberModalBottomSheetState()
    var stopPlanNumber by remember { mutableStateOf("") }

    // 恢复计划弹窗是否显示
    var showResumeSheet by remember { mutableStateOf(false) }
    var resumeSheetState = rememberModalBottomSheetState()
    var resumePlanNumber by remember { mutableStateOf("") }

    // 更新计划弹窗是否显示
    var showUpdateSheet by remember { mutableStateOf(false) }
    var updateSheetState = rememberModalBottomSheetState()
    var updatePlanNumber by remember { mutableStateOf("") }

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // 主要容器
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
        ) {
            // 标题
            item {
                Text(
                    text = "用药计划概览",
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp,
                    color = medKnowPlanPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 50.dp)
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 10.dp, start = 5.dp, end = 5.dp),
                    thickness = 1.dp,
                    color = medKnowSecondary
                )
            }

            // 日历
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = medKnowPlanPrimary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                ) {

                    // 公历
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日")),
                        color = medKnowStandardBlack,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // 农历
                    Text(
                        text = LunarCalendar.today(),
                        color = medKnowStandardBlack,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 已加载计划数
                    Text(
                        text = "当前已加载 ${uiState.totalPlanNumber} 条计划",
                        color = medKnowStandardBlack,
                        fontSize = 15.sp
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

            }

            // 标题
            item {

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "更改计划",
                    color = medKnowPlanPrimary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

            }

            // 简易操作副标题
            item {
                Text(
                    text = "简易操作",
                    color = medKnowSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = medKnowSecondary,
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                )
            }

            // 计划简易操作栏
            item {

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
                ) {

                    // 激活计划
                    SimplePlanOperationItem(
                        onClick = { showActiveSheet = true },
                        onCancelClick = { showActiveSheet = false },
                        onOperatePlan = onActivePlan,
                        onPlanNumberChange = { activePlanNumber = it },
                        planNumber = activePlanNumber,
                        sheetState = activeSheetState,
                        showSheet = showActiveSheet,
                        planOperation = "激活"
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // 暂停计划
                    SimplePlanOperationItem(
                        onClick = { showStopSheet = true },
                        onCancelClick = { showStopSheet = false },
                        onOperatePlan = onStopPlan,
                        onPlanNumberChange = { stopPlanNumber = it },
                        planNumber = stopPlanNumber,
                        sheetState = stopSheetState,
                        showSheet = showStopSheet,
                        planOperation = "暂停"
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // 恢复计划
                    SimplePlanOperationItem(
                        onClick = { showResumeSheet = true },
                        onCancelClick = { showResumeSheet = false },
                        onOperatePlan = onResumePlan,
                        onPlanNumberChange = { resumePlanNumber = it },
                        planNumber = resumePlanNumber,
                        sheetState = resumeSheetState,
                        showSheet = showResumeSheet,
                        planOperation = "恢复"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

            }

            // 复杂操作副标题
            item {
                Text(
                    text = "复杂操作",
                    color = medKnowSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .padding(start = 5.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = medKnowSecondary,
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                )
            }

            // 激活复杂操作栏
            item {

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterHorizontally)
                ) {
                    // 新建计划
                    // 弹窗按钮
                    Button(
                        onClick = { onNavigateToCreateMedicationPlan() },
                        enabled = true,
                        colors = ButtonColors(
                            containerColor = medKnowStandard,
                            contentColor = medKnowStandardBlack,
                            disabledContainerColor = medKnowStandard,
                            disabledContentColor = medKnowStandardBlack
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(30.dp),
                                color = medKnowPlanPrimary
                            )
                            .height(35.dp)
                    ) {
                        Text("创建")
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    // 更新计划
                    ComplexPlanOperationItem(
                        onClick = { showUpdateSheet = true },
                        onCancelClick = { showUpdateSheet = false },
                        onPlanNumberChange = { updatePlanNumber = it },
                        planNumber = updatePlanNumber,
                        sheetState = updateSheetState,
                        showSheet = showUpdateSheet,
                        planOperation = "更新",
                        onSavePlanId = onComplexOperationPlanIdSave,
                        onNavigateToUpdateMedicationPlan = onNavigateToUpdateMedicationPlan
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

            }

            // 计划栏
            items(uiState.medicationPlanList) { medicationPlan ->
                PlanItem(
                    plan = medicationPlan,
                    index = index,
                    backgroundColors= backgroundColors,
                    onNavigateToMedicationInformation = onNavigateToMedicationInformation
                )
                index++
                Spacer(modifier = Modifier.height(5.dp))
            }

        }

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
                    colorFilter = ColorFilter.tint(medKnowStandardBlack),
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
                    .clickable{},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 日程表 logo
                Image(
                    painter = painterResource(id = R.drawable.plan_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(medKnowPlanPrimary),
                    modifier = Modifier
                        .size(30.dp)
                )

                // “日程表”
                Text(
                    text = "日程表",
                    color = medKnowPlanPrimary,
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
                    painter = painterResource(id = R.drawable.article_logo),
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
                    .clickable{ onNavigateToUserPage() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 个人中心 logo
                Image(
                    painter = painterResource(id = R.drawable.article_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )

                // “个人中心”
                Text(
                    text = "个人中心",
                    color = medKnowStandardBlack,
                    fontSize = 12.sp
                )

            }

        }
    }

}

// 用药计划栏
@Composable
fun PlanItem(
    plan: Plan,
    index: Int,
    backgroundColors: List<Color>,
    onNavigateToMedicationInformation: () -> Unit
) {

    val bgColor = backgroundColors[index % backgroundColors.size]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 15.dp)
            .clickable { onNavigateToMedicationInformation() }
    ) {

        // 上方信息栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 计划 Id
            Text(
                text = "计划序号：${plan.planId}",
                color = medKnowStandard,
                fontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
            )

            // 计划状态
            Text(
                text = "状态：" + when(plan.status) {
                    PlanStatus.ACTIVE -> "已激活"
                    PlanStatus.DRAFT -> "草稿"
                    PlanStatus.PAUSED -> "已暂停"
                    PlanStatus.COMPLETED -> "已完成"
                    else -> "已过期"
                },
                color = medKnowStandard,
                fontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp)
            )

            // 药品数量
            Text(
                text = "药品数量：${plan.drugCount}",
                color = medKnowStandard,
                fontSize = 13.sp
            )

        }

        Spacer(modifier = Modifier.height(15.dp))

        // 中部信息栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 主要药品
            Text(
                text = plan.mainDrugName,
                color = medKnowStandard,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
            )

            // 依从率
            Text(
                text = "依从率：${plan.adherenceRate}",
                color = medKnowStandard,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 5.dp)
            )

        }

        Spacer(modifier = Modifier.height(15.dp))

        // 下部信息栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 开始日期
            Text(
                text = "开始于：" + plan.startDate,
                color = medKnowStandard,
                fontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
            )

            // 结束日期
            Text(
                text = "结束于：" + plan.endDate,
                color = medKnowStandard,
                fontSize = 13.sp,
                modifier = Modifier
                    .weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        // 其余信息
        // 备注
        if (plan.notes != null) {
            Text(
                text = "备注：" + plan.notes,
                color = medKnowStandard,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 提醒方式
        Text(
            text = "提醒方式：" + plan.reminderMethods.joinToString("、"),
            color = medKnowStandard,
            fontSize = 13.sp
        )
    }

}

// 计划简易操作栏
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePlanOperationItem(
    onClick: () -> Unit,
    showSheet: Boolean,
    sheetState: SheetState,
    onCancelClick: () -> Unit,
    planNumber: String,
    onOperatePlan: (Int) -> Unit,
    onPlanNumberChange: (String) -> Unit,
    planOperation: String
) {
    // 弹窗按钮
    Button(
        onClick = onClick,
        enabled = true,
        colors = ButtonColors(
            containerColor = medKnowStandard,
            contentColor = medKnowStandardBlack,
            disabledContainerColor = medKnowSecondary,
            disabledContentColor = medKnowStandardBlack,
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(30.dp),
                color = medKnowPlanPrimary
            )
            .height(35.dp)
    ) {
        Text(planOperation)
    }
    // 启用弹窗
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { onCancelClick() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                // 弹窗标题
                Text(
                    text = "请输入计划编号",
                    color = medKnowStandardBlack,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 编号输入框
                OutlinedTextField(
                    value = planNumber,
                    onValueChange = { input ->
                        onPlanNumberChange(input.filter { it.isDigit() })
                    },
                    placeholder = {
                        Text(
                            text = "请输入计划编号，仅支持数字",
                            color = medKnowSecondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 确认键
                Button(
                    enabled = planNumber.isNotBlank(),
                    onClick = {
                        val planId = planNumber.toIntOrNull()
                        if (planId != null) {
                            onOperatePlan(planId)
                            onCancelClick()
                            onPlanNumberChange("")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "确认${planOperation}",
                        color = medKnowStandardBlack
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

// 计划复杂操作栏
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplexPlanOperationItem(
    onClick: () -> Unit,
    showSheet: Boolean,
    sheetState: SheetState,
    onCancelClick: () -> Unit,
    planNumber: String,
    onPlanNumberChange: (String) -> Unit,
    planOperation: String,
    onSavePlanId: (Int) -> Unit,
    onNavigateToUpdateMedicationPlan: () -> Unit
) {
    // 弹窗按钮
    Button(
        onClick = onClick,
        enabled = true,
        colors = ButtonColors(
            containerColor = medKnowStandard,
            contentColor = medKnowStandardBlack,
            disabledContainerColor = medKnowSecondary,
            disabledContentColor = medKnowStandardBlack
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(30.dp),
                color = medKnowPlanPrimary
            )
            .height(35.dp)
    ) {
        Text(planOperation)
    }
    // 启用弹窗
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { onCancelClick() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                // 弹窗标题
                Text(
                    text = "请输入计划编号",
                    color = medKnowStandardBlack,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 编号输入框
                OutlinedTextField(
                    value = planNumber,
                    onValueChange = { input ->
                        onPlanNumberChange(input.filter { it.isDigit() })
                    },
                    placeholder = {
                        Text(
                            text = "请输入计划编号，仅支持数字",
                            color = medKnowSecondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 跳转键
                Button(
                    enabled = planNumber.isNotBlank(),
                    onClick = {
                        val planId = planNumber.toIntOrNull()
                        if (planId != null) {
                            onSavePlanId(planId)
                        }
                        onNavigateToUpdateMedicationPlan()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "下一步",
                        color = medKnowStandardBlack
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun MedicationPlanScreen() {
    MedicationPlanContent(
        uiState = MedicationPlanViewModel.MedicationPlanUiState(
            medicationPlanList = listOf(
                Plan(
                    planId = 1001,
                    status = PlanStatus.DRAFT,
                    drugCount = 1,
                    startDate = "2026-08-07",
                    endDate = "2026-08-10",
                    notes = "这是一条假数据",
                    adherenceRate = 0,
                    reminderMethods = listOf("闹钟"),
                    mainDrugName = "阿莫西林、布洛芬等共2种"
                ),
                Plan(
                    planId = 1002,
                    status = PlanStatus.ACTIVE,
                    drugCount = 1,
                    startDate = "2026-08-10",
                    endDate = "2026-08-20",
                    notes = "这是一条真数据",
                    adherenceRate = 1,
                    reminderMethods = listOf("闹钟", "短信"),
                    mainDrugName = "莫匹罗星等共1种"
                )
            )
        ),
        onCreateMedicationPlan = {_, _, _, _, _ ->},
        onCreateDrug = {_, _, _, _, _, _ ->},
        onMedicationPlanInformation = {},
        onActivePlan = {},
        onStopPlan = {},
        onResumePlan = {},
        onUpdateMedicationPlan = {_, _, _, _, _, _ ->},
        onUpdatePlanDrug = {_, _, _, _, _, _ ->},
        loadMore = {},
        onPlanSort = {},
        onComplexOperationPlanIdSave = {_ ->},
        onNavigateToCreateMedicationPlan = {},
        onNavigateToMedicationInformation= {},
        onNavigateToUpdateMedicationPlan= {},
        onNavigateToFirstPage= {},
        onNavigateToHealthArticle= {},
        onNavigateToVisitNavigation= {},
        onNavigateToUserPage= {}
    )
}
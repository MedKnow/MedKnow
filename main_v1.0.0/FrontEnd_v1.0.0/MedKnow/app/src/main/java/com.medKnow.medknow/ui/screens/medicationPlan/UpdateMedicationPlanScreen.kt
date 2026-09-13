package com.medKnow.medknow.ui.screens.medicationPlan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.DrugCreateMedPlanReq
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.drug.TakeMethod
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel.MedicationPlanViewModel
import com.medKnow.medknow.ui.theme.medKnowPlanPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack
import com.medKnow.medknow.ui.theme.medKnowThird

// 更新用药计划页
@Composable
fun UpdateMedicationPlanScreen(
    viewModel: MedicationPlanViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
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

    UpdateMedicationPlanContent(
        uiState = uiState,
        onUpdatePlanDrug = viewModel::onUpdatePlanDrug,
        onUpdateMedicationPlan = viewModel::onUpdateMedicationPlan,
        onNavigateToMedicationPlan = onNavigateToMedicationPlan
    )

}

// UI 布局
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateMedicationPlanContent(
    uiState: MedicationPlanViewModel.MedicationPlanUiState,
    onUpdatePlanDrug: (String, String, String, String, TakeMethod, String?) -> Unit,
    onUpdateMedicationPlan: (Int, String, String, String?, String?, List<String>) -> Unit,
    onNavigateToMedicationPlan: () -> Unit
) {
    // 新增药品弹窗是否展示
    var showAddDrugSheet by remember { mutableStateOf(false) }
    var addDrugSheetState = rememberModalBottomSheetState()

    // 提醒方式弹窗是否展示
    var showReminderMethodSheet by remember { mutableStateOf(false) }
    var reminderMethodSheetState = rememberModalBottomSheetState()

    // 当前用户即将添加的药品
    var currentDrug by remember {
        mutableStateOf(
            DrugCreateMedPlanReq(
                drugName = "",
                dosage = "",
                dietaryRestrictions = "",
                frequency = "",
                takeMethod = TakeMethod.EMPTY,
                takeTime = ""
            )
        )
    }

    // 当前用户即将更新的计划
    var currentPlan by remember {
        mutableStateOf(
            GetPlanMessageResponse(
                planId = -1,
                status = PlanStatus.DRAFT,
                diagnosis = null,
                adherenceRate = 0,
                drugs = emptyList(),
                startDate = "",
                endDate = "",
                notes = null,
                conflicts = emptyList(),
                reminderMethods = emptyList(),
                todayReminders = emptyList()
            )
        )
    }

    // 当前用户选择的提醒方式
    var currentReminderMethods by remember { mutableStateOf(listOf<String>()) }
    val currentReminderMethod = currentReminderMethods.joinToString("、")

    // 药品服用方式替代量
    var drugTakeMethodReplace = ""

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 40.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 返回键
        Image(
            painter = painterResource(id = R.drawable.articlereturn_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(medKnowPlanPrimary),
            modifier = Modifier
                .size(30.dp)
                .clickable{ onNavigateToMedicationPlan() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 标题
        Text(
            text = "更新日程表",
            color = medKnowPlanPrimary,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // “药品列表”
        Text(
            text = "药品列表",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 药品列表栏
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = medKnowPlanPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .height(270.dp)
                .padding(horizontal = 10.dp, vertical = 15.dp)
        ) {

            items(uiState.updatePlanDrugs) { drug ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(medKnowPlanPrimary)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    // 上部信息栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 药品名称
                        Text(
                            text = drug.drugName,
                            color = medKnowStandard,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                        )
                        // 剂量说明
                        Text(
                            text = "剂量说明：${drug.frequency}，每次 ${drug.dosage}",
                            color = medKnowStandard,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(top = 5.dp, end = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 下部信息栏
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // 服药时间
                        Text(
                            text = "服药时间：" + drug.takeTime,
                            color = medKnowStandard,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .weight(1f)
                        )
                        // 服用方式
                        Text(
                            text = "方式：" + when(drug.takeMethod) {
                                TakeMethod.BEFORE_MEAL -> "饭前"
                                TakeMethod.AFTER_MEAL -> "饭后"
                                TakeMethod.EMPTY_STOMACH -> "空腹"
                                else -> "睡前"
                            },
                            color = medKnowStandard,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(end = 35.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // 忌口
                    if (drug.dietaryRestrictions != null) {
                        Text(
                            text = "忌口：" + drug.dietaryRestrictions,
                            color = medKnowStandard,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

            }

            // 新增药品键位
            item {
                Button(
                    onClick = {showAddDrugSheet = true},
                    enabled = true,
                    colors = ButtonColors(
                        containerColor = medKnowStandard,
                        contentColor = medKnowSecondary,
                        disabledContainerColor = medKnowStandard,
                        disabledContentColor = medKnowSecondary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("+ 新增药品")
                }
                // 启用弹窗
                if (showAddDrugSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddDrugSheet = false },
                        sheetState = addDrugSheetState,
                        modifier = Modifier
                            .imePadding()
                            .navigationBarsPadding()
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 标题
                            item {
                                Text(
                                    text = "新增药品信息",
                                    color = medKnowPlanPrimary,
                                    fontSize = 23.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                            }

                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)
                                ) {
                                    // “药品名称”输入框
                                    Column(
                                        modifier = Modifier
                                            .weight(1f),
                                    ) {
                                        // 标题
                                        Text(
                                            text = "药品名称",
                                            color = medKnowSecondary,
                                            fontSize = 15.sp,
                                            modifier = Modifier
                                                .padding(start = 15.dp)
                                        )

                                        Spacer(modifier = Modifier.height(5.dp))

                                        // 输入框
                                        OutlinedTextField(
                                            value = currentDrug.drugName,
                                            onValueChange = { currentDrug = currentDrug.copy(drugName = it)},
                                            placeholder = {
                                                Text(
                                                    text = "请输入药品名称",
                                                    color = medKnowSecondary,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            singleLine = true,
                                            shape = RoundedCornerShape(20.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedTextColor = medKnowStandardBlack,
                                                unfocusedTextColor = medKnowStandardBlack,
                                                focusedIndicatorColor = medKnowPlanPrimary,
                                                unfocusedIndicatorColor = medKnowSecondary,
                                                focusedContainerColor = medKnowStandard,
                                                unfocusedContainerColor = medKnowStandard
                                            ),
                                            modifier = Modifier
                                                .width(130.dp)
                                        )
                                    }
                                    // “剂量”输入框
                                    Column(
                                        modifier = Modifier
                                    ) {
                                        // 标题
                                        Text(
                                            text = "剂量",
                                            color = medKnowSecondary,
                                            fontSize = 15.sp,
                                            modifier = Modifier
                                                .padding(start = 15.dp)
                                        )

                                        Spacer(modifier = Modifier.height(5.dp))

                                        // 输入框
                                        OutlinedTextField(
                                            value = currentDrug.dosage,
                                            onValueChange = { currentDrug = currentDrug.copy(dosage = it) },
                                            placeholder = {
                                                Text(
                                                    text = "请输入剂量，例如“每日3次",
                                                    color = medKnowSecondary,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = TextFieldDefaults.colors(
                                                focusedTextColor = medKnowStandardBlack,
                                                unfocusedTextColor = medKnowStandardBlack,
                                                focusedIndicatorColor = medKnowPlanPrimary,
                                                unfocusedIndicatorColor = medKnowSecondary,
                                                focusedContainerColor = medKnowStandard,
                                                unfocusedContainerColor = medKnowStandard
                                            ),
                                            modifier = Modifier
                                                .width(200.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            // “服用方式”输入框
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)
                                ) {
                                    // 标题
                                    Text(
                                        text = "服用方式",
                                        color = medKnowSecondary,
                                        fontSize = 15.sp,
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    // 输入框
                                    OutlinedTextField(
                                        value = drugTakeMethodReplace,
                                        onValueChange = {
                                            currentDrug = currentDrug.copy(takeMethod = when(it) {
                                                "饭前" -> TakeMethod.BEFORE_MEAL
                                                "饭后" -> TakeMethod.AFTER_MEAL
                                                "空腹" -> TakeMethod.EMPTY_STOMACH
                                                else -> TakeMethod.BEFORE_SLEEP
                                            }
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                text = "请输入“饭前”、“饭后”、“空腹”、“睡前”任一一种",
                                                color = medKnowSecondary,
                                                fontSize = 12.sp
                                            )
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedTextColor = medKnowStandardBlack,
                                            unfocusedTextColor = medKnowStandardBlack,
                                            focusedIndicatorColor = medKnowPlanPrimary,
                                            unfocusedIndicatorColor = medKnowSecondary,
                                            focusedContainerColor = medKnowStandard,
                                            unfocusedContainerColor = medKnowStandard
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            // “服药时间”输入框
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)
                                ) {
                                    // 标题
                                    Text(
                                        text = "服药时间",
                                        color = medKnowSecondary,
                                        fontSize = 15.sp,
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    // 输入框
                                    OutlinedTextField(
                                        value = currentDrug.takeTime,
                                        onValueChange = { currentDrug = currentDrug.copy(takeTime = it) },
                                        placeholder = {
                                            Text(
                                                text = "多个时间点用逗号“，”分隔",
                                                color = medKnowSecondary,
                                                fontSize = 12.sp
                                            )
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedTextColor = medKnowStandardBlack,
                                            unfocusedTextColor = medKnowStandardBlack,
                                            focusedIndicatorColor = medKnowPlanPrimary,
                                            unfocusedIndicatorColor = medKnowSecondary,
                                            focusedContainerColor = medKnowStandard,
                                            unfocusedContainerColor = medKnowStandard
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            // “忌口”输入框
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)
                                ) {
                                    // 标题
                                    Text(
                                        text = "忌口",
                                        color = medKnowSecondary,
                                        fontSize = 15.sp,
                                        modifier = Modifier
                                            .padding(start = 15.dp)
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    // 输入框
                                    OutlinedTextField(
                                        value = currentDrug.dietaryRestrictions ?: "",
                                        onValueChange = { currentDrug = currentDrug.copy(dietaryRestrictions = it) },
                                        placeholder = {
                                            Text(
                                                text = "药物忌口，例如“忌酒”",
                                                color = medKnowSecondary,
                                                fontSize = 12.sp
                                            )
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedTextColor = medKnowStandardBlack,
                                            unfocusedTextColor = medKnowStandardBlack,
                                            focusedIndicatorColor = medKnowPlanPrimary,
                                            unfocusedIndicatorColor = medKnowSecondary,
                                            focusedContainerColor = medKnowStandard,
                                            unfocusedContainerColor = medKnowStandard
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(15.dp))
                            }

                            // 确认添加键
                            item {
                                Button(
                                    onClick = {
                                        onUpdatePlanDrug(
                                            currentDrug.drugName,
                                            currentDrug.dosage,
                                            currentDrug.frequency,
                                            currentDrug.takeTime,
                                            currentDrug.takeMethod,
                                            currentDrug.dietaryRestrictions
                                        )
                                        currentDrug = DrugCreateMedPlanReq(
                                            drugName = "",
                                            dosage = "",
                                            dietaryRestrictions = "",
                                            frequency = "",
                                            takeMethod = TakeMethod.EMPTY,
                                            takeTime = ""
                                        )
                                    },
                                    enabled = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp)
                                ) {
                                    Text(
                                        text = "确认添加"
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }

        Spacer(modifier = Modifier.height(25.dp))

        // 开始日期
        // 标题
        Text(
            text = "开始日期",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // “开始日期”输入框
        OutlinedTextField(
            value = currentPlan.startDate,
            onValueChange = { currentPlan = currentPlan.copy(startDate = it) },
            placeholder = {
                Text(
                    text = "请输入开始日期，例如“2026-08-10”",
                    color = medKnowSecondary,
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowStandardBlack,
                focusedIndicatorColor = medKnowPlanPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 结束日期
        // 标题
        Text(
            text = "结束日期",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // “结束日期”输入框
        OutlinedTextField(
            value = currentPlan.endDate,
            onValueChange = { currentPlan = currentPlan.copy(endDate = it) },
            placeholder = {
                Text(
                    text = "请输入结束日期，例如“2026-12-10”",
                    color = medKnowSecondary,
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowStandardBlack,
                focusedIndicatorColor = medKnowPlanPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // 备注
        // 标题
        Text(
            text = "备注",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // “备注”输入框
        OutlinedTextField(
            value = currentPlan.notes ?: "",
            onValueChange = { currentPlan = currentPlan.copy(notes = it) },
            placeholder = {
                Text(
                    text = "可选项。请输入备注",
                    color = medKnowSecondary,
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowStandardBlack,
                focusedIndicatorColor = medKnowPlanPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 诊断
        // 标题
        Text(
            text = "诊断信息",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // “诊断信息”输入框
        OutlinedTextField(
            value = currentPlan.diagnosis ?: "",
            onValueChange = { currentPlan = currentPlan.copy(diagnosis = it) },
            placeholder = {
                Text(
                    text = "可选项。请输入就诊时的诊断信息",
                    color = medKnowSecondary,
                    fontSize = 12.sp
                )
            },
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = medKnowStandardBlack,
                unfocusedTextColor = medKnowStandardBlack,
                focusedIndicatorColor = medKnowPlanPrimary,
                unfocusedIndicatorColor = medKnowSecondary,
                focusedContainerColor = medKnowStandard,
                unfocusedContainerColor = medKnowStandard
            ),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 提醒方式
        // 标题
        Text(
            text = "提醒方式",
            color = medKnowSecondary,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 提醒方式选择框
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable{ showReminderMethodSheet = true }
        ) {
            OutlinedTextField(
                value = currentReminderMethod,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = "请选择提醒方式",
                        color = medKnowSecondary,
                        fontSize = 12.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = medKnowStandardBlack,
                    unfocusedTextColor = medKnowStandardBlack,
                    focusedIndicatorColor = medKnowPlanPrimary,
                    unfocusedIndicatorColor = medKnowSecondary,
                    focusedContainerColor = medKnowStandard,
                    unfocusedContainerColor = medKnowStandard,
                    disabledContainerColor = medKnowStandard,
                    disabledTextColor = medKnowSecondary,
                    disabledIndicatorColor = medKnowSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReminderMethodSheet = true }
            )
        }

        // 提醒方式弹窗
        if (showReminderMethodSheet) {
            ModalBottomSheet(
                onDismissRequest = { showReminderMethodSheet = false },
                sheetState = reminderMethodSheetState,
            ) {
                // 标题
                Text(
                    text = "请选择提醒方式",
                    color = medKnowPlanPrimary,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // “闹钟”
                Text(
                    text = "闹钟",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = if ( "闹钟" in currentReminderMethods ) {
                        medKnowPlanPrimary
                    } else {
                        medKnowSecondary
                    },
                    modifier = Modifier
                        .clickable{
                            currentReminderMethods = if ( "闹钟" in currentReminderMethods ) {
                                currentReminderMethods - "闹钟"
                            } else {
                                currentReminderMethods + "闹钟"
                            }
                        }
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    color = medKnowThird,
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // “短信”
                Text(
                    text = "短信",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = if ( "短信" in currentReminderMethods ) {
                        medKnowPlanPrimary
                    } else {
                        medKnowSecondary
                    },
                    modifier = Modifier
                        .clickable{
                            currentReminderMethods = if ( "短信" in currentReminderMethods ) {
                                currentReminderMethods - "短信"
                            } else {
                                currentReminderMethods + "短信"
                            }
                        }
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(
                    thickness = 2.dp,
                    color = medKnowThird,
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // “App推送”
                Text(
                    text = "应用推送",
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    color = if ( "应用推送" in currentReminderMethods ) {
                        medKnowPlanPrimary
                    } else {
                        medKnowSecondary
                    },
                    modifier = Modifier
                        .clickable{
                            currentReminderMethods = if ( "应用推送" in currentReminderMethods ) {
                                currentReminderMethods - "应用推送"
                            } else {
                                currentReminderMethods + "应用推送"
                            }
                        }
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(50.dp))

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 确认键
        Button(
            onClick = {
                onUpdateMedicationPlan(
                    uiState.complexOperationPlanId,
                    currentPlan.startDate,
                    currentPlan.endDate,
                    currentPlan.notes,
                    currentPlan.diagnosis,
                    currentReminderMethods
                )
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "确认更新"
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun UpdateMedicationPlanScreenPreview() {
    UpdateMedicationPlanContent(
        uiState = MedicationPlanViewModel.MedicationPlanUiState(

        ),
        onUpdateMedicationPlan = {_, _, _, _, _, _ ->},
        onUpdatePlanDrug = {_, _, _, _, _, _ ->},
        onNavigateToMedicationPlan = {}
    )
}
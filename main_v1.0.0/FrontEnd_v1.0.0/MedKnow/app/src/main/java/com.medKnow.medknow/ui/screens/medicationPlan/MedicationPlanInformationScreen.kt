package com.medKnow.medknow.ui.screens.medicationPlan

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.medicationPlans.PlanStatus
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.Conflicts
import com.medKnow.medknow.model.medicationPlans.createMedicationPlan.Type
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.DrugGetPlanMessageRes
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.GetPlanMessageResponse
import com.medKnow.medknow.model.medicationPlans.getPlanMessage.TodayReminders
import com.medKnow.medknow.ui.screens.medicationPlan.medicationPlanViewModel.MedicationPlanViewModel
import com.medKnow.medknow.ui.theme.medKnowError
import com.medKnow.medknow.ui.theme.medKnowPlanPrimary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack
import com.medKnow.medknow.ui.theme.medKnowThird

// 计划详情页
@Composable
fun MedicationPlanInformationScreen(
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

    MedicationPlanInformationContent(
        uiState = uiState,
        onNavigateToMedicationPlan = onNavigateToMedicationPlan
    )

}

// UI 布局
@Composable
fun MedicationPlanInformationContent(
    uiState: MedicationPlanViewModel.MedicationPlanUiState,
    onNavigateToMedicationPlan: () -> Unit
) {

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 40.dp, bottom = 40.dp)
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

        // 标题
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "日程表详情",
            color = medKnowPlanPrimary,
            fontSize = 25.sp
        )

        // 用药计划内容
        Spacer(modifier = Modifier.height(30.dp))

        // 药品清单
        // 标题
        Text(
            text = "药品清单",
            color = medKnowPlanPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 5.dp)
        )

        HorizontalDivider(
            color = medKnowThird,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 内容
        // 药品清单
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = medKnowPlanPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(medKnowStandard)
                .height(350.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            items(uiState.selectedPlan.drugs) { drug ->
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

                    Spacer(modifier = Modifier.height(10.dp))

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
                            text = "服用方式：" + when(drug.takeMethod) {
                                "BEFORE_MEAL" -> "饭前"
                                "AFTER_MEAL" -> "饭后"
                                "BEFORE_SLEEP" -> "睡前"
                                else -> "空腹"
                            },
                            color = medKnowStandard,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(end = 35.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 忌口
                    if (drug.dietaryRestrictions != null) {
                        Text(
                            text = "忌口：" + drug.dietaryRestrictions,
                            color = medKnowStandard,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 验证标识
                    if (drug.verified) {
                        Text(
                            text = "当前药品未经系统数据库认证，请谨慎服用",
                            color = medKnowError,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 风险标识
                    if (drug.dosageRisk) {
                        Text(
                            text = "当前药品剂量超出说明书最大限制，请确保当前设置正确",
                            color = medKnowError,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                }

                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        // 计划 Id
        PlanInformationItem(
            title = "计划编号",
            value = "${uiState.selectedPlan.planId}"
        )

        // 计划状态
        PlanInformationItem(
            title = "计划状态",
            value = when(uiState.selectedPlan.status) {
                PlanStatus.DRAFT -> "草稿"
                PlanStatus.COMPLETED -> "完成"
                PlanStatus.PAUSED -> "暂停"
                PlanStatus.ACTIVE -> "激活"
                else -> "已过期"
            }
        )

        // 开始日期
        PlanInformationItem(
            title = "开始日期",
            value = uiState.selectedPlan.startDate
        )

        // 结束日期
        PlanInformationItem(
            title = "结束日期",
            value = uiState.selectedPlan.endDate
        )

        // 备注
        PlanInformationItem(
            title = "备注",
            value = uiState.selectedPlan.notes ?: "暂无备注"
        )

        // 诊断信息
        PlanInformationItem(
            title = "诊断信息",
            value = uiState.selectedPlan.diagnosis ?: "暂无诊断信息"
        )

        // 依从率
        PlanInformationItem(
            title = "依从率",
            value = "${uiState.selectedPlan.adherenceRate}"
        )

        // 提醒方式
        PlanInformationItem(
            title = "提醒方式",
            value = reminderMethodTranslate(
                uiState.selectedPlan.reminderMethods
            ).joinToString("、")
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 冲突列表
        Text(
            text = "冲突列表",
            color = medKnowPlanPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 5.dp)
        )

        HorizontalDivider(
            color = medKnowThird,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = medKnowPlanPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(medKnowStandard)
                .height(200.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            items(uiState.selectedPlan.conflicts) { conflict ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(medKnowPlanPrimary)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    // 药品名称
                    Text(
                        text = conflict.drugs.joinToString("、"),
                        maxLines = 1,
                        color = medKnowStandard,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 冲突类型
                    Text(
                        text = "冲突类型：" + when(conflict.type) {
                            Type.INTERVAL -> "间隔冲突"
                            else -> "禁忌冲突"
                        },
                        color = medKnowStandard,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 5.dp, end = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 冲突信息
                    Text(
                        text = "冲突描述：" + conflict.message,
                        color = medKnowStandard,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                }

                Spacer(modifier = Modifier.height(10.dp))

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 今日全部提醒
        Text(
            text = "今日全部提醒",
            color = medKnowPlanPrimary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = 5.dp)
        )

        HorizontalDivider(
            color = medKnowThird,
            thickness = 2.dp,
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 5.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = medKnowPlanPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(medKnowStandard)
                .height(200.dp)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            items(uiState.selectedPlan.todayReminders) { todayReminder ->
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .background(medKnowPlanPrimary)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    // 提醒 Id
                    Text(
                        text = "提醒编号：${todayReminder.reminderId}",
                        color = medKnowStandard,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // 药品名称
                    Text(
                        text = todayReminder.drugName,
                        color = medKnowStandard,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(top = 5.dp, end = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 计划提醒时间
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // 计划提醒时间
                        Text(
                            text = "提醒时间：" + todayReminder.scheduledTime,
                            color = medKnowStandard,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .weight(1f)
                        )
                        // 提醒状态
                        Text(
                            text = "提醒状态：" + when(todayReminder.status) {
                                ReminderStatus.TAKEN -> "已服药"
                                ReminderStatus.TRIGGERED -> "已触发"
                                ReminderStatus.PENDING -> "待提醒"
                                ReminderStatus.IGNORED -> "已忽略"
                                else -> "已过期"
                            },
                            color = medKnowStandard,
                            fontSize = 12.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                }

                Spacer(modifier = Modifier.height(10.dp))

            }
        }

    }

}

// 计划详情栏
@Composable
fun PlanInformationItem(
    title: String,
    value: String
) {
    Spacer(modifier = Modifier.height(30.dp))

    // 标题
    Text(
        text = title,
        color = medKnowPlanPrimary,
        fontSize = 20.sp,
        modifier = Modifier
            .padding(start = 5.dp)
    )

    HorizontalDivider(
        color = medKnowThird,
        thickness = 2.dp,
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp, top = 5.dp)
    )

    // 内容
    Text(
        text = value,
        color = medKnowStandardBlack,
        fontSize = 15.sp,
        modifier = Modifier
            .padding(start = 15.dp, top = 15.dp)
    )
}

// 提醒方式转换
@Composable
fun reminderMethodTranslate(
    reminderMethods: List<String>
): List<String> {
    return reminderMethods.map { reminderMethod ->
        when (reminderMethod) {
            "ALARM" -> "闹钟"
            "SMS" -> "短信"
            else -> "应用推送"
        }
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun MedicationPlanInformationScreenPreview() {
    MedicationPlanInformationContent(
        uiState = MedicationPlanViewModel.MedicationPlanUiState(
            selectedPlan = GetPlanMessageResponse(
                planId = 1002,
                status = PlanStatus.ACTIVE,
                startDate = "2026-08-10",
                endDate = "2026-08-20",
                notes = "这是一条真数据",
                diagnosis = "医生还有的救",
                adherenceRate = 1,
                reminderMethods = listOf("ALARM", "SMS"),
                drugs = listOf (
                    DrugGetPlanMessageRes(
                        drugId = 100001,
                        drugName = "阿莫西林",
                        dosage = "1 片",
                        frequency = "每日 3 次",
                        takeTime = "08:00,17:00,22:00",
                        takeMethod = "BEFORE_MEAL",
                        dietaryRestrictions = "忌辛辣",
                        verified = true,
                        dosageRisk = true
                    ),
                    DrugGetPlanMessageRes(
                        drugId = 100001,
                        drugName = "阿莫西林",
                        dosage = "1 片",
                        frequency = "每日 3 次",
                        takeTime = "08:00,17:00,22:00",
                        takeMethod = "BEFORE_MEAL",
                        dietaryRestrictions = "忌辛辣",
                        verified = true,
                        dosageRisk = true
                    ),
                    DrugGetPlanMessageRes(
                        drugId = 100001,
                        drugName = "阿莫西林",
                        dosage = "1 片",
                        frequency = "每日 3 次",
                        takeTime = "08:00,17:00,22:00",
                        takeMethod = "BEFORE_MEAL",
                        dietaryRestrictions = "忌辛辣",
                        verified = true,
                        dosageRisk = true
                    )
                ),
                conflicts = listOf(
                    Conflicts(
                        drugs = listOf("布洛芬", "蛇胆口服液"),
                        type = Type.INTERVAL,
                        message = "服用完布洛芬后，不能立即服用蛇胆口服液"
                    ),
                    Conflicts(
                        drugs = listOf("布洛芬", "蛇胆口服液"),
                        type = Type.INTERVAL,
                        message = "服用完布洛芬后，不能立即服用蛇胆口服液"
                    ),
                    Conflicts(
                        drugs = listOf("布洛芬", "蛇胆口服液"),
                        type = Type.INTERVAL,
                        message = "服用完布洛芬后，不能立即服用蛇胆口服液"
                    )
                ),
                todayReminders = listOf (
                    TodayReminders(
                        reminderId = 1000002,
                        drugName = "阿莫西林",
                        scheduledTime = "2026-07-14 08:00",
                        status = ReminderStatus.TAKEN
                    ),
                    TodayReminders(
                        reminderId = 1000002,
                        drugName = "阿莫西林",
                        scheduledTime = "2026-07-14 08:00",
                        status = ReminderStatus.TAKEN
                    ),
                    TodayReminders(
                        reminderId = 1000002,
                        drugName = "阿莫西林",
                        scheduledTime = "2026-07-14 08:00",
                        status = ReminderStatus.TAKEN
                    )
                )
            )
        ),
        onNavigateToMedicationPlan = {}
    )
}
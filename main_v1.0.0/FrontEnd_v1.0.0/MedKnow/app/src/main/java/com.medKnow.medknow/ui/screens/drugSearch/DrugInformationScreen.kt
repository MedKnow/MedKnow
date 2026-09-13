package com.medKnow.medknow.ui.screens.drugSearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.drugSearch.medicationMessage.GetMedicationMessageResponse
import com.medKnow.medknow.ui.screens.drugSearch.drugSearchViewModel.DrugSearchViewModel
import com.medKnow.medknow.ui.theme.medKnowCollected
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 药品详情页
@Composable
fun DrugInformationScreen(
    viewModel: DrugSearchViewModel = hiltViewModel(),
    onNavigateToDrugSearch: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DrugInformationContent(
        uiState = uiState,
        onDrugCollection = viewModel::onDrugCollection,
        onNavigateToDrugSearch = onNavigateToDrugSearch
    )

}

// UI 布局
@Composable
fun DrugInformationContent(
    uiState: DrugSearchViewModel.DrugSearchUiState,
    onDrugCollection: (Int) -> Unit,
    onNavigateToDrugSearch: () -> Unit
) {
    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp)
    ) {

        // 返回键
        Image(
            painter = painterResource(id = R.drawable.articlereturn_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(medKnowPrimary),
            modifier = Modifier
                .padding(top = 50.dp, start = 10.dp)
                .size(30.dp)
                .clickable { onNavigateToDrugSearch() }
        )

        // 药品详情
        if (uiState.isLoadingDrug) {
            Text(
                text = "正在加载药品详情",
                color = medKnowSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else if (uiState.loadingDrugErrorMessage != null) {
            Text(
                text = uiState.loadingDrugErrorMessage,
                color = medKnowSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        } else {
            LazyColumn {

                // 标题
                item {
                    Text(
                        text = "药品详情",
                        color = medKnowPrimary,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, bottom = 40.dp)
                    )
                }

                item {

                    Spacer(modifier = Modifier.height(15.dp))

                    // 是否添加到药箱
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 230.dp)
                            .clickable { onDrugCollection(uiState.selectedDrug.drugId) }
                    ) {
                        // 添加图标
                        Image(
                            painter = painterResource(id = R.drawable.collection_logo),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(
                                if(uiState.selectedDrug.isCollected) {
                                    medKnowCollected
                                } else {
                                    medKnowSecondary
                                }
                            ),
                            modifier = Modifier
                                .size(25.dp)
                        )
                        // 文字说明
                        Text(
                            text = "添加至药箱",
                            color = if(uiState.selectedDrug.isCollected) {
                                medKnowCollected
                            } else {
                                medKnowSecondary
                            },
                            fontSize = 15.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 5.dp, top = 5.dp)
                        )
                    }
                }

                // 药品学名
                item {
                    DrugInformationItem(
                        title = "药品名称",
                        content = uiState.selectedDrug.drugName
                    )
                }

                // 药品通用名
                item {
                    DrugInformationItem(
                        title = "通用名",
                        content = uiState.selectedDrug.genericName
                    )
                }

                // 药品类别
                item {
                    if (uiState.selectedDrug.category != null) {
                        DrugInformationItem(
                            title = "种类",
                            content = uiState.selectedDrug.category
                        )
                    }
                }

                // 适应症
                item {
                    if (uiState.selectedDrug.indications != null) {
                        DrugInformationItem(
                            title = "适应症",
                            content = uiState.selectedDrug.indications
                        )
                    }
                }

                // 用法用量
                item {
                    if (uiState.selectedDrug.dosage != null) {
                        DrugInformationItem(
                            title = "用法用量",
                            content = uiState.selectedDrug.dosage
                        )
                    }
                }

                // 不良反应
                item {
                    if (uiState.selectedDrug.sideEffects != null) {
                        DrugInformationItem(
                            title = "不良反应",
                            content = uiState.selectedDrug.sideEffects
                        )
                    }
                }

                // 禁忌
                item {
                    if (uiState.selectedDrug.contraindications != null) {
                        DrugInformationItem(
                            title = "禁忌",
                            content = uiState.selectedDrug.contraindications
                        )
                    }
                }

                // 注意事项
                item {
                    if (uiState.selectedDrug.precautions != null) {
                        DrugInformationItem(
                            title = "注意事项",
                            content = uiState.selectedDrug.precautions
                        )
                    }
                }

                // 存储条件
                item {
                    if (uiState.selectedDrug.storage != null) {
                        DrugInformationItem(
                            title = "存储条件",
                            content = uiState.selectedDrug.storage
                        )
                    }
                }
            }
        }

    }

}

// 药品指标栏
@Composable
fun DrugInformationItem(
    title: String,
    content: String
) {
    // 标题
    Text(
        text = title,
        color = medKnowPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 15.dp, bottom = 15.dp)
    )
    // 内容
    Text(
        text = content,
        color = medKnowStandardBlack,
        fontSize = 15.sp,
        textAlign = TextAlign.Start,
        lineHeight = 25.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, top = 5.dp, bottom = 5.dp, end = 15.dp)
    )
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun DrugInformationScreenPreview() {
    DrugInformationContent(
        uiState = DrugSearchViewModel.DrugSearchUiState(
            selectedDrug = GetMedicationMessageResponse(
                drugId = 10001,
                drugName = "阿莫西林胶囊",
                genericName = "阿莫西林",
                category = "抗生素",
                indications = "适用于敏感菌（不产β内酰胺酶菌株）引起的下列感染：呼吸道感染、泌尿生殖道感染、皮肤软组织感染等。",
                dosage = "成人一次0.5g，每6～8小时1次；小儿一日剂量按体重20～40mg/kg，每8小时1次。",
                sideEffects = "恶心、呕吐、腹泻及假膜性肠炎等胃肠道反应；皮疹、药物热和哮喘等过敏反应。",
                contraindications = "对青霉素类药物过敏者禁用；传染性单核细胞增多症患者禁用。",
                precautions = "肾功能不全者需根据肌酐清除率调整剂量；有哮喘、枯草热等过敏性疾病史者慎用。",
                storage = "密封，在阴凉干燥处保存（不超过20℃）。",
                isCollected = true
            )
        ),
        onDrugCollection = {},
        onNavigateToDrugSearch = {}
    )
}
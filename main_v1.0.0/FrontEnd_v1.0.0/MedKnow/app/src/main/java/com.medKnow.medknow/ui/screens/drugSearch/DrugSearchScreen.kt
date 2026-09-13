package com.medKnow.medknow.ui.screens.drugSearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medKnow.medknow.R
import com.medKnow.medknow.model.drugSearch.medicationSearch.Results
import com.medKnow.medknow.ui.screens.drugSearch.drugSearchViewModel.DrugSearchViewModel
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 药品搜索页
@Composable
fun DrugSearchScreen(
    viewModel: DrugSearchViewModel = hiltViewModel(),
    onNavigateToFirstPage: () -> Unit,
    onNavigateToDrugInformation: () -> Unit
) {

    // 点击搜索键收回键盘
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DrugSearchContent(
        uiState = uiState,
        onKeywordChange = viewModel::onKeywordChange,
        onSearch = {
            keyboardController?.hide()
            focusManager.clearFocus()
            viewModel.onSearch()
        },
        onSuggestionsClicked = viewModel::onSuggestionsClicked,
        onNavigateToFirstPage = onNavigateToFirstPage,
        onNavigateToDrugInformation = onNavigateToDrugInformation
    )

}

// UI 布局
@Composable
fun DrugSearchContent(
    uiState: DrugSearchViewModel.DrugSearchUiState,
    onKeywordChange: (String) -> Unit,
    onSearch: () -> Unit,
    onSuggestionsClicked: (Int) -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToDrugInformation: () -> Unit
) {

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 5.dp, end = 20.dp)
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 返回键
            Image(
                painter = painterResource(id = R.drawable.articlereturn_logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(medKnowPrimary),
                modifier = Modifier
                    .padding(top = 10.dp, end = 20.dp)
                    .size(35.dp)
                    .clickable { onNavigateToFirstPage() }
            )

            // 药品搜索框
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = medKnowPrimary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(end = 10.dp)

            ) {

                // 药品名输入栏
                OutlinedTextField(
                    value = uiState.searchKeyword,
                    onValueChange = { onKeywordChange(it) },
                    placeholder = {
                        Text(
                            text = "请输入药品名",
                            color = medKnowSecondary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = medKnowPrimary,
                        unfocusedIndicatorColor = medKnowStandard,
                        unfocusedContainerColor = medKnowStandard,
                        focusedTextColor = medKnowPrimary,
                        focusedIndicatorColor = medKnowStandard,
                        focusedContainerColor = medKnowStandard
                    ),
                    modifier = Modifier
                        .weight(1f)
                )

                val focusRequester = remember { FocusRequester() }

                // 搜索键
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(medKnowPrimary)
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            focusRequester.requestFocus()   // 先索取焦点，挤掉输入框
                            onSearch()
                        }
                        .focusRequester(focusRequester)
                        .focusable()
                        .border(
                            width = 1.dp,
                            color = medKnowPrimary,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {

                    // 搜索 logo
                    Image(
                        painter = painterResource(id = R.drawable.drugsearch_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                    )

                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 推荐结果列表
        if (uiState.suggestions.isNotEmpty() && !uiState.isSearching) {
            LazyColumn {
                items(uiState.suggestions, key = { it.drugId }) { suggestion ->

                    SuggestionItem(
                        selectedDrug = suggestion,
                        onSuggestionsClicked = { onSuggestionsClicked(suggestion.drugId) },
                        onNavigateToDrugInformation = onNavigateToDrugInformation
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else if (uiState.searchKeyword.isNotEmpty() && !uiState.isSearching){
            Text(
                text = "未找到相关药品",
                color = medKnowSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 250.dp)
            )
        } else if (uiState.isSearching) {
            Text(
                text = "正在加载搜索结果",
                color = medKnowSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 250.dp)
            )
        } else {
            return@Column
        }

    }
}

// 推荐结果栏
@Composable
fun SuggestionItem(
    selectedDrug: Results,
    onSuggestionsClicked: (Int) -> Unit,
    onNavigateToDrugInformation: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSuggestionsClicked(selectedDrug.drugId)
                onNavigateToDrugInformation()
            }
    ) {
        // 搜索 logo
        Image(
            painter = painterResource(id = R.drawable.drugsearch_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(medKnowPrimary),
            modifier = Modifier
                .size(25.dp)
        )

        // 药品名称
        Text(
            text = selectedDrug.drugName,
            color = medKnowStandardBlack,
            fontSize = 15.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 20.dp, top = 4.dp)
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
fun DrugSearchScreenPreview() {
    DrugSearchContent(
        uiState = DrugSearchViewModel.DrugSearchUiState(),
        onKeywordChange = {_ ->},
        onSearch = {},
        onSuggestionsClicked = {_ ->},
        onNavigateToDrugInformation = {},
        onNavigateToFirstPage = {}
    )
}
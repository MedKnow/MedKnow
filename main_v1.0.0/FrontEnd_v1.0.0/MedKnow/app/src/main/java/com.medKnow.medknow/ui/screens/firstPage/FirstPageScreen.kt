package com.medKnow.medknow.ui.screens.firstPage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medKnow.medknow.R
import com.medKnow.medknow.model.ReminderStatus
import com.medKnow.medknow.model.checkins.Reminders
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import com.medKnow.medknow.ui.screens.firstPage.firstPageViewModel.FirstPageViewModel
import com.medKnow.medknow.ui.theme.medKnowArticlePrimary
import com.medKnow.medknow.ui.theme.medKnowError
import com.medKnow.medknow.ui.theme.medKnowNavigationBackground
import com.medKnow.medknow.ui.theme.medKnowPrimary
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack
import java.time.LocalTime
import java.time.Duration.between
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 首页
@Composable
fun FirstPageScreen(
    viewModel: FirstPageViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToDrugSearch: () -> Unit,
    onNavigateToArticleInformation: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToUserPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit
) {

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.isRemindersLoading, uiState.isArticlesLoading) {
        if (uiState.isRemindersLoading || uiState.isArticlesLoading) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    FirstPageContent(
        uiState = uiState,
        onInputKeyword = viewModel::onInputKeyword,
        onDrugSearch = viewModel::onDrugSearch,
        onNavigateToDrugSearch = onNavigateToDrugSearch,
        onNavigateToArticleInformation = onNavigateToArticleInformation,
        onNavigateToMedicationPlan = onNavigateToMedicationPlan,
        onNavigateToVisitNavigation = onNavigateToVisitNavigation,
        onNavigateToUserPage = onNavigateToUserPage,
        onNavigateToHealthArticle = onNavigateToHealthArticle
    )

}

// UI 布局
@Composable
fun FirstPageContent(
    uiState: FirstPageViewModel.FirstPageUiState,
    onInputKeyword: (String) -> Unit,
    onDrugSearch: () -> Unit,
    onNavigateToDrugSearch: () -> Unit,
    onNavigateToArticleInformation: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToUserPage: () -> Unit,
    onNavigateToHealthArticle: () -> Unit
) {

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // 主要容器
        Column(
            modifier = Modifier
                .background(medKnowStandard)
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 药品搜索框
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = medKnowPrimary,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(end = 10.dp)

            ) {

                // 药品名输入栏
                OutlinedTextField(
                    value = uiState.searchDrugKeyword,
                    onValueChange = onInputKeyword,
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
                        .clickable { onNavigateToDrugSearch() }
                )

                // 搜索键
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(medKnowPrimary)
                        .clickable { onDrugSearch() }
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

            Spacer(modifier = Modifier.height(30.dp))

            // 日程表临近提醒栏

            // 标题
            Text(
                text = "即将到来的提醒",
                color = medKnowPrimary,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 提醒内容栏
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp)
                    .border(
                        width = 1.dp,
                        color = medKnowPrimary,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {

                when {

                    uiState.isRemindersLoading -> {
                        Text(
                            text = "加载中...",
                            color = medKnowSecondary
                        )
                    }

                    uiState.currentReminderPreview.isEmpty() -> {
                        Text(
                            text = "暂无近期提醒",
                            color = medKnowSecondary
                        )
                    }

                    else -> {
                        uiState.currentReminderPreview.forEach { reminder ->
                            ReminderItem(
                                reminderName = reminder.drugName,
                                reminderScheduledTime = reminder.scheduledTime
                            )
                        }
                    }

                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            // 最热科普文章提醒栏

            // 标题
            Text(
                text = "猜您想看",
                color = medKnowArticlePrimary,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 文章内容栏
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .border(
                        width = 1.dp,
                        color = medKnowArticlePrimary,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {

                when {

                    uiState.isArticlesLoading -> {
                        Text(
                            text = "加载中...",
                            color = medKnowSecondary
                        )
                    }

                    uiState.currentTopArticlePreview.isEmpty() -> {
                        Text(
                            text = "暂无最新文章",
                            color = medKnowSecondary
                        )
                    }

                    else -> {
                        uiState.currentTopArticlePreview.forEach { topArticle ->
                            TopArticleItem(
                                articleTitle = topArticle.title,
                                readCount = topArticle.readCount,
                                publishTime = topArticle.publishTime,
                                coverImage = topArticle.coverImage,
                                onNavigateToArticleInformation = onNavigateToArticleInformation
                            )
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(30.dp))

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
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 首页 logo
                Image(
                    painter = painterResource(id = R.drawable.firstpage_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(medKnowPrimary),
                    modifier = Modifier
                        .size(30.dp)
                )
                // “首页”
                Text(
                    text = "首页",
                    color = medKnowPrimary,
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

// 临近提醒栏
@Composable
fun ReminderItem(
    reminderName: String,
    reminderScheduledTime: String
) {
    // 整体容器
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 20.dp)
    ) {

        // 提醒 logo
        Image(
            painter = painterResource(id = R.drawable.reminder_logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(medKnowPrimary),
            modifier = Modifier
                .size(40.dp)
        )

        // 提醒具体内容
        Column(
            modifier = Modifier
                .weight(1f),
        ) {

            // 提醒的药品名
            Text(
                text = reminderName,
                color = medKnowStandardBlack,
                fontSize = 17.sp
            )

            // 提醒的计划时间
            Text(
                text = reminderScheduledTime,
                color = medKnowSecondary,
                fontSize = 12.sp
            )

        }

        // 计划规定时间距当前时间的总分钟数
        // 解析计划时间
        val scheduled = LocalTime.parse(reminderScheduledTime)
        val now = LocalTime.now()

        // 计算计划规定时间距当前时间的总分钟数
        val totalMinutes = between(now, scheduled).toMinutes()

        Text(
            text = "$totalMinutes",
            color = medKnowError,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = "分钟后",
            color = medKnowStandardBlack,
            fontSize = 17.sp,
            modifier = Modifier
                .padding(top = 5.dp)
        )

    }

    // 下划线
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp, end = 10.dp, bottom = 20.dp),
        thickness = 1.dp,
        color = medKnowSecondary
    )

}

// 最热文章栏
@Composable
fun TopArticleItem(
    articleTitle: String,
    readCount: Int,
    publishTime: String,
    coverImage: String,
    onNavigateToArticleInformation: () -> Unit
) {

    // 解析计划时间
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val published = LocalDateTime.parse(publishTime, formatter)
    val now = LocalDateTime.now()

    // 计算计划规定时间距当前时间的总小时数
    val totalHours = between(published, now).toHours()

    // 父容器
    Column(
        modifier = Modifier
            .clickable{ onNavigateToArticleInformation() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp)
        ) {

            // 火热 logo
            Image(
                painter = painterResource(id = R.drawable.hotarticle_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(25.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            // 发布时间距当前时间的总小时数
            Text(
                text = "${totalHours}小时前",
                color = medKnowError,
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(top = 3.dp)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            // 文章主要内容
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                // 文章标题
                Text(
                    text = articleTitle,
                    color = medKnowStandardBlack,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                )

                // 文章发布时间与阅读量
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 30.dp)
                ) {

                    // 文章发布时间
                    Text(
                        text = publishTime,
                        color = medKnowSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp)
                    )

                    // 文章阅读量
                    Image(
                        painter = painterResource(id = R.drawable.hotarticle_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                    )

                    Text(
                        text = "浏览 $readCount",
                        color = medKnowError,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.width(10.dp))

            // 文章封面
            if (coverImage.isNotEmpty()) {
                AsyncImage(
                    model = coverImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RectangleShape)
                        .padding(end = 12.dp)
                        .offset(y = (-20).dp)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.articlecoverimage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RectangleShape)
                        .padding(end = 12.dp)
                        .offset(y = (-20).dp)
                )
            }

        }

        // 下划线
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .offset(y = (-35).dp),
            thickness = 1.dp,
            color = medKnowSecondary
        )
    }

}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun FirstPageScreenPreview() {
    FirstPageContent(
        uiState = FirstPageViewModel.FirstPageUiState(
            currentReminderPreview = listOf(
                Reminders(
                    reminderId = 101,
                    planId = 1003,
                    drugName = "布洛芬",
                    scheduledTime = "15:44",
                    status = ReminderStatus.PENDING
                ),
                Reminders(
                    reminderId = 102,
                    planId = 1004,
                    drugName = "蛇胆口服液",
                    scheduledTime = "17:30",
                    status = ReminderStatus.TAKEN
                )
            ),
            currentTopArticlePreview = listOf(
                Article(
                    articleId = 3031,
                    title = "枸杞为何能补血？",
                    summary = "因为...",
                    coverImage = "",
                    category = "饮食",
                    author = "曲高和寡",
                    publishTime = "2026-08-10 23:03",
                    readCount = 10086,
                    likeCount = 12306,
                    isLiked = true,
                    isCollected = false,
                ),
                Article(
                    articleId = 3034,
                    title = "当你坚持一月每天只睡3小时时，身体会有这些变化",
                    summary = "众所周知...",
                    coverImage = "",
                    category = "养生",
                    author = "哦吼吼",
                    publishTime = "2026-08-15 19:50",
                    readCount = 123,
                    likeCount = 120,
                    isLiked = false,
                    isCollected = true,
                )
            ),
            errorMessage = "搜索失败"
        ),
        onInputKeyword = {},
        onDrugSearch = {},
        onNavigateToDrugSearch = {},
        onNavigateToArticleInformation = {},
        onNavigateToMedicationPlan = {},
        onNavigateToVisitNavigation = {},
        onNavigateToUserPage = {},
        onNavigateToHealthArticle = {}
    )
}
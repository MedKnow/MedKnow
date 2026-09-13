package com.medKnow.medknow.ui.screens.healthArticle

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import com.medKnow.medknow.ui.screens.healthArticle.healthArticleViewModel.HealthArticleViewModel
import com.medKnow.medknow.ui.theme.medKnowArticleCategory
import com.medKnow.medknow.ui.theme.medKnowArticlePrimary
import com.medKnow.medknow.ui.theme.medKnowCollected
import com.medKnow.medknow.ui.theme.medKnowNavigationBackground
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandard
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 科普文章页
@Composable
fun HealthArticleScreen(
    viewModel: HealthArticleViewModel = hiltViewModel(),
    onNavigateToArticleInformation: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit,
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

    HealthArticleContent(
        uiState = uiState,
        onCategorySelect = viewModel::onCategorySelect,
        loadMore = viewModel::loadMore,
        onArticleSelect = viewModel::onArticleSelect,
        onArticleCollection = viewModel::onArticleCollection,
        onNavigateToFirstPage = onNavigateToFirstPage,
        onNavigateToMedicationPlan = onNavigateToMedicationPlan,
        onNavigateToVisitNavigation = onNavigateToVisitNavigation,
        onNavigateToUserPage = onNavigateToUserPage,
        onNavigateToArticleInformation = onNavigateToArticleInformation
    )

}

// UI 布局
@Composable
fun HealthArticleContent(
    uiState: HealthArticleViewModel.HealthArticleUiState,
    onCategorySelect: (String) -> Unit,
    loadMore: () -> Unit,
    onArticleSelect: (Int) -> Unit,
    onArticleCollection: () -> Unit,
    onNavigateToFirstPage: () -> Unit,
    onNavigateToMedicationPlan: () -> Unit,
    onNavigateToVisitNavigation: () -> Unit,
    onNavigateToUserPage: () -> Unit,
    onNavigateToArticleInformation: () -> Unit
) {

    // 监听列表是否滚动到底部
    val listState = rememberLazyListState()

    // 当接近底部且满足条件时触发加载更多
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            // 距底部还差 3 个 item 时提前加载
            lastVisibleItem >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            loadMore()
        }
    }

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 主要容器
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, end = 5.dp, top = 40.dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 标题
            Text(
                text = "分类",
                color = medKnowArticlePrimary,
                fontSize = 30.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                // “全部”分类栏
                CategoryItem(
                    category = "全部",
                    isSelected = uiState.selectedCategory == "全部",
                    onCategorySelect = onCategorySelect,
                    categoryLogo = R.drawable.all_logo
                )

                Spacer(modifier = Modifier.width(10.dp))

                // “饮食”分类栏
                CategoryItem(
                    category = "饮食",
                    isSelected = uiState.selectedCategory == "饮食",
                    onCategorySelect = onCategorySelect,
                    categoryLogo = R.drawable.eating_logo
                )

                Spacer(modifier = Modifier.width(10.dp))

                // “运动”分类栏
                CategoryItem(
                    category = "运动",
                    isSelected = uiState.selectedCategory == "运动",
                    onCategorySelect = onCategorySelect,
                    categoryLogo = R.drawable.sport_logo
                )

                Spacer(modifier = Modifier.width(10.dp))

                // “疾病”分类栏
                CategoryItem(
                    category = "疾病",
                    isSelected = uiState.selectedCategory == "疾病",
                    onCategorySelect = onCategorySelect,
                    categoryLogo = R.drawable.disease_logo
                )

                Spacer(modifier = Modifier.width(10.dp))

                // "自然"分类栏
                CategoryItem(
                    category = "自然",
                    isSelected = uiState.selectedCategory == "自然",
                    onCategorySelect = onCategorySelect,
                    categoryLogo = R.drawable.nature_logo
                )

            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, top = 20.dp),
                thickness = 2.dp,
                color = medKnowSecondary
            )

            // 文章列表
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                when {
                    uiState.isLoading -> {
                        Text(
                            text = "正在加载文章...",
                            color = medKnowArticlePrimary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            // 加载文章列表，可滚动
                            items(uiState.sortedArticles, key = { it.articleId }) { article ->
                                ArticleItem(
                                    id = article.articleId,
                                    title = article.title,
                                    author = article.author,
                                    category = article.category,
                                    coverImage = article.coverImage,
                                    publishTime = article.publishTime,
                                    readCount = article.readCount,
                                    isCollected = article.isCollected,
                                    categoryLogo = when(article.category) {
                                        "全部" -> R.drawable.all_logo
                                        "疾病" -> R.drawable.disease_logo
                                        "自然" -> R.drawable.nature_logo
                                        "运动" -> R.drawable.sport_logo
                                        else -> R.drawable.eating_logo
                                    },
                                    onArticleSelect = onArticleSelect,
                                    onArticleCollection = onArticleCollection,
                                    onNavigateToArticleInformation = onNavigateToArticleInformation
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // 列表末尾加载状态提示
                            item {
                                when {
                                    uiState.isLoadingMore -> {
                                        Text(
                                            text = "正在加载更多",
                                            color = medKnowArticlePrimary,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    !uiState.hasMore -> {
                                        Text(
                                            text = "已经到底了",
                                            color = medKnowStandardBlack,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                }
                            }

                        }
                    }

                }
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
                    .clickable{},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 科普 logo
                Image(
                    painter = painterResource(id = R.drawable.article_logo),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(medKnowArticlePrimary),
                    modifier = Modifier
                        .size(30.dp)
                )

                // “科普”
                Text(
                    text = "科普",
                    color = medKnowArticlePrimary,
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


// 分类选择栏
@Composable
fun CategoryItem(
    category: String,
    isSelected: Boolean,
    onCategorySelect: (String) -> Unit,
    @DrawableRes categoryLogo: Int
) {

    // 外边框
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = medKnowStandard)
            .clickable { onCategorySelect(category) }
            .border(
                width = 1.dp,
                color = if(isSelected) {
                    medKnowArticlePrimary
                } else {
                    medKnowSecondary
                },
                shape = RoundedCornerShape(12.dp),
            )
            .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 分类对应 logo
        Image(
            painter = painterResource(id = categoryLogo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (isSelected) {
                    medKnowArticlePrimary
                } else {
                    medKnowSecondary
                }
            ),
            modifier = Modifier
                .size(30.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 分类名称
        Text(
            text = category,
            color = if(isSelected) {
                medKnowArticlePrimary
            } else {
                medKnowSecondary
            },
            fontSize = 15.sp
        )
    }

}

// 文章栏
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleItem(
    id: Int,
    title: String,
    category: String,
    publishTime: String,
    coverImage: String,
    readCount: Int,
    author: String,
    isCollected: Boolean,
    onArticleSelect: (Int) -> Unit,
    onArticleCollection: () -> Unit,
    @DrawableRes categoryLogo: Int,
    onNavigateToArticleInformation: () -> Unit
) {

    // 外边框
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    onArticleSelect(id)
                    onNavigateToArticleInformation()
                }
            )
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = medKnowArticlePrimary,
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {

        // 文章所述分类
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
        ) {
            // 分类 logo
            Image(
                painter = painterResource(id = categoryLogo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(medKnowArticleCategory),
                modifier = Modifier
                    .size(30.dp)
            )

            // 分类名称
            Text(
                text = category,
                color = medKnowArticleCategory,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 5.dp, start = 3.dp)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        // 文章标题以及封面
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 文章标题
            Text(
                text = title,
                color = medKnowStandardBlack,
                fontSize = 17.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )
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

        // 其他相关信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            // 发布时间
            Text(
                text = publishTime,
                color = medKnowSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 6.dp)
            )
            // 阅读量
            Text(
                text = "浏览 $readCount",
                color = medKnowSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp, bottom = 5.dp, start = 10.dp, top = 5.dp)
            )
            // 作者
            Text(
                text = author,
                color = medKnowSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(end = 10.dp, bottom = 5.dp, top = 5.dp)
                    .weight(1f)
            )
            // 收藏按钮
            Image(
                painter = painterResource(id = R.drawable.collection_logo),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    if(isCollected) {
                        medKnowCollected
                    } else {
                        medKnowSecondary
                    }
                ),
                modifier = Modifier
                    .clickable { onArticleCollection() }
                    .size(30.dp)
                    .padding(bottom = 5.dp)
            )
        }

    }

}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun HealthArticleScreenPreview() {

    HealthArticleContent(
        uiState = HealthArticleViewModel.HealthArticleUiState(
            selectedCategory = "运动",
            sortedArticles = listOf(
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
                    category = "自然",
                    author = "哦吼吼",
                    publishTime = "2026-08-15 19:50",
                    readCount = 123,
                    likeCount = 120,
                    isLiked = false,
                    isCollected = true,
                ),
                Article(
                    articleId = 3035,
                    title = "长期坚持跑步，身体会有哪些变化",
                    summary = "因为...",
                    coverImage = "",
                    category = "运动",
                    author = "Sing",
                    publishTime = "2026-08-26 20:03",
                    readCount = 106,
                    likeCount = 1206,
                    isLiked = true,
                    isCollected = false,
                )
            )
        ),
        onCategorySelect = {},
        loadMore = {},
        onArticleSelect = {},
        onArticleCollection = {},
        onNavigateToFirstPage = {},
        onNavigateToMedicationPlan = {},
        onNavigateToVisitNavigation = {},
        onNavigateToUserPage = {},
        onNavigateToArticleInformation = {}
    )

}
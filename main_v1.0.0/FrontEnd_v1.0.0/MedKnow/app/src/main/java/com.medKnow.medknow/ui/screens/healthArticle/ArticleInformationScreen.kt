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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.medKnow.medknow.R
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleInformation.RelatedArticle
import com.medKnow.medknow.ui.screens.healthArticle.healthArticleViewModel.HealthArticleViewModel
import com.medKnow.medknow.ui.theme.medKnowArticlePrimary
import com.medKnow.medknow.ui.theme.medKnowNavigationBackground
import com.medKnow.medknow.ui.theme.medKnowSecondary
import com.medKnow.medknow.ui.theme.medKnowStandardBlack

// 文章详情页
@Composable
fun ArticleInformationScreen (
    viewModel: HealthArticleViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToHealthArticle: () -> Unit
){

    // 将 _uiState 属性委托给 uiState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 处理一次性导航事件
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            onNavigateToHome()
            viewModel.onNavigatedToHome()
        }
    }

    ArticleInformationContent(
        uiState = uiState,
        onArticleSelect = viewModel::onArticleSelect,
        onArticleCollection = viewModel::onArticleCollection,
        onArticleLike = viewModel::onArticleLike,
        onArticleShare = viewModel::onArticleShare,
        onNavigateToHealthArticle = onNavigateToHealthArticle
    )

}

// UI 布局
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleInformationContent(
    uiState: HealthArticleViewModel.HealthArticleUiState,
    onArticleSelect: (Int) -> Unit,
    onArticleCollection: () -> Unit,
    onArticleLike: () -> Unit,
    onArticleShare: (String) -> Unit,
    onNavigateToHealthArticle: () -> Unit
) {

    // 分享弹窗是否显示
    var showShareSheet by remember { mutableStateOf(false) }
    var sheetState = rememberModalBottomSheetState()

    // 父容器
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 主要容器
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 50.dp, end = 10.dp)
                .weight(1f)
        ) {

            // 返回键
            item{
                Image(
                    painter = painterResource(id = R.drawable.articlereturn_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable{ onNavigateToHealthArticle() }
                )
            }

            // 文章标题
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = uiState.selectedArticle.title,
                    color = medKnowStandardBlack,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // 文章相关信息
            item {
                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 发布时间
                    Text(
                        text = uiState.selectedArticle.publishTime,
                        color = medKnowSecondary,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 5.dp)
                    )
                    // 浏览量
                    Text(
                        text = "浏览 ${uiState.selectedArticle.readCount}",
                        color = medKnowSecondary,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 30.dp)
                    )
                    // 作者名称
                    Text(
                        text = uiState.selectedArticle.author,
                        color = medKnowArticlePrimary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(end = 20.dp)
                    )
                }
            }

            // 作者简介
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "作者简介：" + uiState.selectedArticle.authorTitle,
                    color = medKnowSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            // 标签
            item {

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // 文字
                    Text(
                        text = "标签：",
                        color = medKnowSecondary,
                        fontSize = 12.sp
                    )
                    // 各个标签
                    uiState.selectedArticle.tags.forEach { tag ->
                        Text(
                            text = "#$tag",
                            color = medKnowSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )
                    }
                }

            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 10.dp),
                    thickness = 1.dp,
                    color = medKnowSecondary
                )
            }

            // 文章正文
            item {
                Text(
                    text = uiState.selectedArticle.content,
                    color = medKnowStandardBlack,
                    fontSize = 17.sp,
                    lineHeight = 28.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp, top = 10.dp),
                    thickness = 1.dp,
                    color = medKnowSecondary
                )
            }

            // “相关推荐”
            item {
                Text(
                    text = "相关推荐",
                    color = medKnowArticlePrimary,
                    fontSize = 23.sp,
                )

                Spacer(modifier = Modifier.height(5.dp))

            }

            // 推荐文章
            if (uiState.selectedArticle.relatedArticles.isNotEmpty() ) {
                items(uiState.selectedArticle.relatedArticles) { relatedArticle ->

                    Spacer(modifier = Modifier.height(10.dp))

                    RelatedArticleItem(
                        id = relatedArticle.articleId,
                        title = relatedArticle.title,
                        coverImage = relatedArticle.coverImage,
                        onArticleSelect = onArticleSelect
                    )
                }
            }
        }

        // 底部文章评论栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(medKnowNavigationBackground)
                .clip(RectangleShape)
                .padding(vertical = 15.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // 点赞
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onArticleLike() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ArticleOperationItem(
                    isOperated = uiState.selectedArticle.isLiked,
                    operationCount = uiState.selectedArticle.likeCount,
                    operationLogo = R.drawable.like_logo
                )
            }

            // 收藏
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable{ onArticleCollection() }
            ) {
                ArticleOperationItem(
                    isOperated = uiState.selectedArticle.isCollected,
                    operationCount = uiState.selectedArticle.collectCount,
                    operationLogo = R.drawable.collection_logo
                )
            }

            // 分享
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showShareSheet = true },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // 分享 logo
                    Image(
                        painter = painterResource(id = R.drawable.share_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    // 当前文章分享数
                    Text(
                        text = "${uiState.selectedArticle.shareCount}",
                        color = medKnowStandardBlack,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(top = 5.dp)
                    )
                }
            }
        }

    }

    // 分享方式半屏弹窗
    if (showShareSheet) {

        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 100.dp)
            ) {
                // 标题
                Text(
                    text = "请选择分享方式",
                    color = medKnowStandardBlack,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 各分享方式
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    // 微信
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onArticleShare("WECHAT")},
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShareItem(
                            shareWay = "微信",
                            shareLogo = R.drawable.wechat
                        )
                    }

                    // QQ
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onArticleShare("QQ") },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShareItem(
                            shareWay = "QQ",
                            shareLogo = R.drawable.qq_logo
                        )
                    }

                    // 其他
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onArticleShare("COPY_LINK") },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShareItem(
                            shareWay = "其他",
                            shareLogo = R.drawable.else_logo
                        )
                    }

                }
            }
        }
    }
}

// 推荐文章栏
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RelatedArticleItem(
    id: Int,
    title: String,
    coverImage: String,
    onArticleSelect: (Int) -> Unit
) {
    // 外边框
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = { onArticleSelect(id) }
            )
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = medKnowArticlePrimary,
                shape = RoundedCornerShape(12.dp)
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 文章标题以及封面
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 文章标题
            Text(
                text = title,
                color = medKnowStandardBlack,
                fontSize = 17.sp,
                maxLines = 2,
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
                        .offset(y = 5.dp)
                )
            }

        }

    }
}

// 分享方式栏
@Composable
fun ShareItem(
    shareWay: String,
    @DrawableRes shareLogo: Int
) {
    // 分享方式 logo
    Image(
        painter = painterResource(id = shareLogo),
        contentDescription = null,
        modifier = Modifier
            .size(45.dp)
    )

    Spacer(modifier = Modifier.height(5.dp))

    // 分享方式名称
    Text(
        text = shareWay,
        color = medKnowStandardBlack,
        fontSize = 15.sp
    )
}

// 文章评论栏
@Composable
fun ArticleOperationItem(
    isOperated: Boolean,
    @DrawableRes operationLogo: Int,
    operationCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // 操作 logo
        Image(
            painter = painterResource(id = operationLogo),
            colorFilter = ColorFilter.tint(
                if (isOperated) {
                    medKnowArticlePrimary
                } else {
                    medKnowSecondary
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .size(25.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        // 当前操作数
        Text(
            text = "$operationCount",
            color = if (isOperated) {
                medKnowArticlePrimary
            } else {
                medKnowSecondary
            },
            fontSize = 15.sp,
            modifier = Modifier
                .padding(top = 5.dp)
        )
    }
}

// UI 测试函数
@Preview(showBackground = true)
@Composable
fun ArticleInformationScreenPreview() {
    ArticleInformationContent(
        uiState = HealthArticleViewModel.HealthArticleUiState(
            selectedArticle = ArticleInformationResponse(
                articleId = 3034,
                title = "当你坚持一月每天只睡3小时时，身体会有这些变化",
                content = "然后是中青年。这里有一个问题：我国法律中是否有“中青年”的明确法律定义？" +
                        "可能并没有像未成年和老年那样有专门法律给出定义。我国法律通常将年龄段划分为未成年人（0-17周岁）和成年人（18周岁以上），" +
                        "然后老年人权益保障法定义了60岁以上为老年人。那么中青年可",
                coverImage = "https://10.152.12.36",
                category = "养生",
                author = "哦吼吼",
                authorTitle = "在职医生",
                publishTime = "2026-08-15 19:50",
                readCount = 123,
                likeCount = 120,
                collectCount = 89,
                shareCount = 50,
                isLiked = true,
                isCollected = true,
                tags = listOf (
                    "熬夜",
                    "身体变化"
                ),
                relatedArticles = listOf (
                    RelatedArticle(
                        articleId = 3035,
                        title = "为什么熬夜对人体损伤最大?",
                        coverImage = ""
                    ),
                    RelatedArticle(
                        articleId = 3036,
                        title = "当你熬夜时，有哪些器官在超负荷运作？",
                        coverImage = ""
                    )
                )
            )
        ),
        onArticleSelect = {},
        onArticleCollection = {},
        onArticleLike = {},
        onArticleShare = {},
        onNavigateToHealthArticle = {}
    )
}
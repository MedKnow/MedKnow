package com.medKnow.medknow.data.repository.fakeRepository

import com.medKnow.medknow.data.repository.ArticleRepository
import com.medKnow.medknow.model.healthArticle.ArticleCollectResponse
import com.medKnow.medknow.model.healthArticle.ArticleLikeResponse
import com.medKnow.medknow.model.healthArticle.ArticleShareRequest
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleInformation.RelatedArticle
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryQuery
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryResponse
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.RecommendedArticleListResponse
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

// 全局错误标志
object FakeErrorArticleFlags {

    // 是否有权限
    var isUnauthorized: Boolean = false
    // 网络是否正常
    var isNetworkError: Boolean = false
    // 通用服务器错误
    var simulateServerError: Boolean = false

}

// 假_科普文章模块
class FakeArticleRepository : ArticleRepository {

    // 日期时间格式化器
    val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // 虚假的科普文章数据库
    val articles = mutableListOf (
        ArticleInformationResponse(
            articleId = 3031,
            title = "枸杞为何能补血？",
            content = "因为...",
            coverImage = "https://localhost:8090",
            category = "饮食",
            author = "曲高和寡",
            authorTitle = "湖南省作家协会会长",
            publishTime = "2026-08-10 23:03",
            readCount = 10086,
            likeCount = 12306,
            collectCount = 12315,
            shareCount = 1086,
            isLiked = true,
            isCollected = false,
            tags = listOf (
                "枸杞",
                "补血"
            ),
            relatedArticles = listOf (
                RelatedArticle(
                    articleId = 3032,
                    title = "为什么上厕所久蹲后突然站起会短暂失明？",
                    coverImage = "https://192.128.10.13",
                ),
                RelatedArticle(
                    articleId = 3033,
                    title = "除了枸杞，还有那些能补血？",
                    coverImage = "https://192.168.20.15"
                )
            )
        ),
        ArticleInformationResponse(
            articleId = 3034,
            title = "当你坚持一月每天只睡3小时时，身体会有这些变化",
            content = "众所周知...",
            coverImage = "https://10.152.12.36",
            category = "养生",
            author = "哦吼吼",
            authorTitle = "在职医生",
            publishTime = "2026-08-15 19:50",
            readCount = 123,
            likeCount = 120,
            collectCount = 89,
            shareCount = 50,
            isLiked = false,
            isCollected = true,
            tags = listOf (
                "熬夜",
                "身体变化"
            ),
            relatedArticles = listOf (
                RelatedArticle(
                    articleId = 3035,
                    title = "为什么熬夜对人体损伤最大?",
                    coverImage = "https://weng@163.com"
                ),
                RelatedArticle(
                    articleId = 3036,
                    title = "当你熬夜时，有哪些器官在超负荷运作？",
                    coverImage = "https://1563@qq.com"
                )
            )
        )
    )

    // 推荐文章列表
    override suspend fun recommendedArticleList(): Result<RecommendedArticleListResponse> {
        delay(200)

        return when {

            // 500 服务端异常
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 200 成功
            else -> Result.success (
                RecommendedArticleListResponse(
                    articles = articles.map { article ->
                        Article(
                            articleId = article.articleId,
                            title = article.title,
                            summary = article.content.take(60),
                            coverImage = article.coverImage,
                            category = article.category,
                            author = article.author,
                            publishTime = article.publishTime,
                            readCount = article.readCount,
                            likeCount = article.likeCount,
                            isLiked = article.isLiked,
                            isCollected = article.isCollected
                        )
                    },
                    total = articles.size
                )
            )

        }

    }

    // 文章列表（按分类筛选）
    override suspend fun articleListSortByCategory(query: ArticleListSortByCategoryQuery): Result<ArticleListSortByCategoryResponse> {
        delay(500)

        val sortedArticles = articles.filter { it.category == query.category }

        return when {

            // 500 服务器内部错误
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 200 成功
            else -> {
                Result.success (
                    ArticleListSortByCategoryResponse (
                        articles = sortedArticles.map { sortedArticle ->
                            Article(
                                articleId = sortedArticle.articleId,
                                title = sortedArticle.title,
                                summary = sortedArticle.content.take(60),
                                coverImage = sortedArticle.coverImage,
                                category = query.category ?: sortedArticle.category,
                                author = sortedArticle.author,
                                publishTime = sortedArticle.publishTime,
                                readCount = sortedArticle.readCount,
                                likeCount = sortedArticle.likeCount,
                                isLiked = sortedArticle.isLiked,
                                isCollected = sortedArticle.isCollected
                            )
                        },
                        total = sortedArticles.size,
                        page = query.page ?: 1,
                        size = query.size ?: 10,
                        totalPages = sortedArticles.size / (query.size ?: 10)
                    )
                )
            }
        }
    }

    // 文章详情
    override suspend fun articleInformation(articleId: Int): Result<ArticleInformationResponse> {
        delay(300)

        val legalArticle = articles.find { it.articleId == articleId }

        return when {

            // 500 服务端异常
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 404 文章不存在
            legalArticle == null -> Result.failure(Exception("文章不存在"))

            // 200 成功
            else -> Result.success (
                ArticleInformationResponse(
                    articleId = legalArticle.articleId,
                    title = legalArticle.title,
                    content = legalArticle.content,
                    coverImage = legalArticle.coverImage,
                    category = legalArticle.category,
                    author = legalArticle.author,
                    authorTitle = legalArticle.authorTitle,
                    publishTime = legalArticle.publishTime,
                    readCount = legalArticle.readCount,
                    likeCount = legalArticle.likeCount,
                    collectCount = legalArticle.collectCount,
                    shareCount = legalArticle.shareCount,
                    isLiked = legalArticle.isLiked,
                    isCollected = legalArticle.isCollected,
                    tags = legalArticle.tags,
                    relatedArticles = legalArticle.relatedArticles
                )
            )
        }
    }

    // 收藏文章
    override suspend fun articleCollect(articleId: Int): Result<ArticleCollectResponse> {
        delay(100)

        val legalArticle = articles.find { it.articleId == articleId }
        val legalArticleIndex = articles.indexOfFirst { it.articleId == articleId }

        return when {

            // 500 服务端异常
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 404 文章不存在
            legalArticle == null -> Result.failure(Exception("文章不存在"))

            // 401 未登录
            FakeErrorArticleFlags.isUnauthorized -> Result.failure(Exception("未登录或 Token 为空"))

            // 200 成功
            else -> {
                articles[legalArticleIndex] = legalArticle.copy(isCollected = true)

                Result.success (
                    ArticleCollectResponse(
                        isCollected = true
                    )
                )
            }
        }
    }

    // 点赞文章
    override suspend fun articleLike(articleId: Int): Result<ArticleLikeResponse> {
        delay(100)

        val legalArticle = articles.find { it.articleId == articleId }
        val legalArticleIndex = articles.indexOfFirst { it.articleId == articleId }

        return when {

            // 500 服务端异常
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 404 文章不存在
            legalArticle == null -> Result.failure(Exception("文章不存在"))

            // 401 未登录
            FakeErrorArticleFlags.isUnauthorized -> Result.failure(Exception("未登录或 Token 为空"))

            // 200 成功
            else -> {

                articles[legalArticleIndex] = legalArticle.copy(isLiked = true)
                articles[legalArticleIndex] = legalArticle.copy(likeCount = legalArticle.likeCount + 1)

                Result.success (
                    ArticleLikeResponse(
                        isLiked = true,
                        likeCount = articles[legalArticleIndex].likeCount
                    )
                )
            }
        }

    }

    // 分享文章
    override suspend fun articleShare(articleId: Int, request: ArticleShareRequest): Result<Unit> {
        delay(300)

        val legalTargetPlatform = setOf("WECHAT", "QQ", "COPY_LINK")
        val legalArticle = articles.find { it.articleId == articleId }

        return when {

            // 500 服务端异常
            FakeErrorArticleFlags.simulateServerError -> Result.failure(Exception("服务器繁忙，请稍后重试"))

            // 400 目标平台不支持
            request.target !in legalTargetPlatform -> Result.failure(Exception("不支持的分享平台"))

            // 404 文章不存在
            legalArticle == null -> Result.failure(Exception("文章不存在"))

            // 200 成功
            else -> Result.success(Unit)
        }

    }
    
}
package com.medKnow.medknow.data.remote.dto

import com.medKnow.medknow.model.healthArticle.ArticleCollectResponse
import com.medKnow.medknow.model.healthArticle.ArticleLikeResponse
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleInformation.RelatedArticle
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryResponse
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.RecommendedArticleListResponse

// 文章
data class ArticleDto (

    // 文章ID
    val articleId: Int,

    // 文章标题
    val title: String,

    // 文章摘要。前60个字符
    val summary: String,

    // 封面图片URL
    val coverImage: String,

    // 分类。中药 / 西药 / 养生 / 疾病 / 饮食
    val category: String,

    // 作者或来源
    val author: String,

    // 发布时间。格式yyyy-MMMM-dd HH:mm:ss
    val publishTime: String,

    // 阅读量
    val readCount: Int,

    // 点赞数
    val likeCount: Int,

    // 当前用户是否已点赞（未登录用户固定为 false）
    val isLiked: Boolean = false,

    // 当前用户是否已收藏（未登录用户固定为 false）
    val isCollected: Boolean = false,

) {
    fun toDomain(): Article {
        return Article(
            articleId = articleId,
            title = title,
            summary = summary,
            coverImage = coverImage,
            category = category,
            author = author,
            publishTime = publishTime,
            readCount = readCount,
            likeCount = likeCount,
            isLiked = isLiked,
            isCollected = isCollected,
        )
    }
}

// 推荐文章列表 data
data class RecommendedArticleListData (

    // 文章列表
    val articles: List<ArticleDto>,

    // 总文章数
    val total: Int

) {
    fun toDomain(): RecommendedArticleListResponse {
        return RecommendedArticleListResponse(
            articles = articles.map { it.toDomain() },
            total = total
        )
    }
}

// 文章列表 data
data class ArticleListSortByCategoryData(

    // 文章列表。按发布时间降序排列
    val articles: List<ArticleDto>,

    // 该分类下的总文章数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页数量
    val size: Int,

    // 总页数
    val totalPages: Int,

) {
    fun toDomain(): ArticleListSortByCategoryResponse {
        return ArticleListSortByCategoryResponse(
            articles = articles.map { it.toDomain() },
            total = total,
            page = page,
            size = size,
            totalPages = totalPages
        )
    }
}

// 相关推荐文章 dto
data class RelatedArticleDto(

    // 相关文章ID
    val articleId: Int,

    // 相关文章标题
    val title: String,

    // 相关文章封面图片URL
    val coverImage: String

) {
    fun toDomain(): RelatedArticle {
        return RelatedArticle(
            articleId = articleId,
            title = title,
            coverImage = coverImage
        )
    }
}

// 文章详情 data
data class ArticleInformationData(

    // 文章ID
    val articleId: Int,

    // 文章标题
    val title: String,

    // 文章正文。支持 MarkDown 或纯文本
    val content: String,

    // 封面图片URL
    val coverImage: String,

    // 分类。中药 / 西药 / 养生 / 疾病 / 饮食
    val category: String,

    // 作者
    val author: String,

    // 作者职称/简介
    val authorTitle: String,

    // 发布时间
    val publishTime: String,

    // 阅读量
    val readCount: Int,

    // 点赞数
    val likeCount: Int,

    // 收藏数
    val collectCount: Int,

    // 分享数
    val shareCount: Int,

    // 当前用户是否已点赞
    val isLiked: Boolean = false,

    // 当前用户是否已收藏
    val isCollected: Boolean = false,

    // 文章标签
    val tags: List<String> = emptyList(),

    // 相关文章推荐。最多 3 篇
    val relatedArticles: List<RelatedArticleDto> = emptyList()

) {
    fun toDomain(): ArticleInformationResponse {
        return ArticleInformationResponse(
            articleId = articleId,
            title = title,
            content = content,
            coverImage = coverImage,
            category = category,
            author = author,
            authorTitle = authorTitle,
            publishTime = publishTime,
            readCount = readCount,
            likeCount = likeCount,
            collectCount = collectCount,
            shareCount = shareCount,
            isLiked = isLiked,
            isCollected = isCollected,
            tags = tags,
            relatedArticles = relatedArticles.map { it.toDomain() },
        )
    }
}

// 点赞文章 data
data class ArticleLikeData(

    // 操作后的点赞状态。 true 已点赞 / false 未点赞
    val isLiked: Boolean,

    // 操作后的总点赞数。前端直接更新显示
    val likeCount: Int

) {
    fun toDomain(): ArticleLikeResponse {
        return ArticleLikeResponse(
            isLiked = isLiked,
            likeCount = likeCount
        )
    }
}

// 收藏文章 data
data class ArticleCollectData(

    // 操作后的收藏状态。true 已收藏 / false 未收藏
    val isCollected: Boolean

) {
    fun toDomain(): ArticleCollectResponse {
        return ArticleCollectResponse(
            isCollected = isCollected,
        )
    }
}
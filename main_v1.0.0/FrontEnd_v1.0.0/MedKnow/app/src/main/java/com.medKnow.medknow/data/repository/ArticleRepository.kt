package com.medKnow.medknow.data.repository

import com.medKnow.medknow.model.healthArticle.ArticleCollectResponse
import com.medKnow.medknow.model.healthArticle.ArticleLikeResponse
import com.medKnow.medknow.model.healthArticle.ArticleShareRequest
import com.medKnow.medknow.model.healthArticle.articleInformation.ArticleInformationResponse
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryQuery
import com.medKnow.medknow.model.healthArticle.articleListSortByCategory.ArticleListSortByCategoryResponse
import com.medKnow.medknow.model.healthArticle.recommendedArticleList.RecommendedArticleListResponse

// 科普文章模块
interface ArticleRepository {

    // 接口23：推荐文章列表
    suspend fun recommendedArticleList(): Result<RecommendedArticleListResponse>

    // 接口24；文章列表（按分类筛选）
    suspend fun articleListSortByCategory(query: ArticleListSortByCategoryQuery): Result<ArticleListSortByCategoryResponse>

    // 接口25：文章详情
    suspend fun articleInformation(articleId: Int): Result<ArticleInformationResponse>

    // 接口26：收藏/取消收藏文章
    suspend fun articleCollect(articleId: Int): Result<ArticleCollectResponse>

    // 接口27：点赞/取消点赞文章
    suspend fun articleLike(articleId: Int): Result<ArticleLikeResponse>

    // 接口28：分享文章
    suspend fun articleShare(articleId: Int, request: ArticleShareRequest): Result<Unit>

}
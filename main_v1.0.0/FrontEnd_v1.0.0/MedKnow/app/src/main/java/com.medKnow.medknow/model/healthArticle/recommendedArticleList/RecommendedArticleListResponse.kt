package com.medKnow.medknow.model.healthArticle.recommendedArticleList

// 推荐文章列表
data class RecommendedArticleListResponse (

    // 文章列表
    val articles: List<Article>,

    // 总文章数
    val total: Int

)
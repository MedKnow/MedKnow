package com.medKnow.medknow.model.healthArticle.articleListSortByCategory

import com.medKnow.medknow.model.healthArticle.recommendedArticleList.Article

// 文章列表（按分类筛选，返回响应）
data class ArticleListSortByCategoryResponse (

    // 文章列表。按发布时间降序排列
    val articles: List<Article>,

    // 该分类下的总文章数
    val total: Int,

    // 当前页码
    val page: Int,

    // 每页数量
    val size: Int,

    // 总页数
    val totalPages: Int

)
package com.medKnow.medknow.model.healthArticle.articleListSortByCategory

// 文章列表（按分类筛选，请求体）
data class ArticleListSortByCategoryQuery (

    // 分类筛选：中药 / 西药 / 养生 / 疾病 / 饮食。不传则返回全部
    val category: String?,

    // 页码。默认 1
    val page: Int?,

    // 每页数量
    val size: Int?

)
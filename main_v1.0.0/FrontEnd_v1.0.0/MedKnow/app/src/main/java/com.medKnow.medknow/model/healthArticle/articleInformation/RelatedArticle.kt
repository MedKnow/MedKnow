package com.medKnow.medknow.model.healthArticle.articleInformation

// 相关推荐文章
data class RelatedArticle (

    // 相关文章ID
    val articleId: Int,

    // 相关文章标题
    val title: String,

    // 相关文章封面图片URL
    val coverImage: String

)
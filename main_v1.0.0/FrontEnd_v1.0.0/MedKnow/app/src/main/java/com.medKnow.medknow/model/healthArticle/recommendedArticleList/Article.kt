package com.medKnow.medknow.model.healthArticle.recommendedArticleList

// 文章
data class Article (

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
    val isLiked: Boolean,

    // 当前用户是否已收藏（未登录用户固定为 false）
    val isCollected: Boolean

)
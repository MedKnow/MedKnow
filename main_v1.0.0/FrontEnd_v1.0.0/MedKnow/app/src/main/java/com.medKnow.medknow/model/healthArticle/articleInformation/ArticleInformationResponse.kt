package com.medKnow.medknow.model.healthArticle.articleInformation

// 文章详情
data class ArticleInformationResponse (

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
    val isLiked: Boolean,

    // 当前用户是否已收藏
    val isCollected: Boolean,

    // 文章标签
    val tags: List<String>,

    // 相关文章推荐。最多 3 篇
    val relatedArticles: List<RelatedArticle>

)
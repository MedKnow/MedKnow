package com.medKnow.medknow.model.healthArticle

// 收藏文章
data class ArticleCollectResponse (

    // 操作后的收藏状态。true 已收藏 / false 未收藏
    val isCollected: Boolean

)
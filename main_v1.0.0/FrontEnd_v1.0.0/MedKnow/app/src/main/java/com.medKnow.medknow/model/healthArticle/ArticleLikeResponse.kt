package com.medKnow.medknow.model.healthArticle

// 点赞文章
data class ArticleLikeResponse (

    // 操作后的点赞状态。 true 已点赞 / false 未点赞
    val isLiked: Boolean,

    // 操作后的总点赞数。前端直接更新显示
    val likeCount: Int

)
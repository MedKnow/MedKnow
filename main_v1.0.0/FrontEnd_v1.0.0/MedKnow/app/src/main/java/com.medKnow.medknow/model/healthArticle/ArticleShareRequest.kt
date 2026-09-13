package com.medKnow.medknow.model.healthArticle

// 分享文章
data class ArticleShareRequest (

    // 分项目标平台。 WECHAT 微信 / QQ QQ / COPY_LINK 复制链接
    val target: String

)
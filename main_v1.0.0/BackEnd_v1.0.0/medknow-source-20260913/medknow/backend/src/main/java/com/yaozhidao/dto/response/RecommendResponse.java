package com.yaozhidao.dto.response;

import java.util.List;

/** 文章推荐响应（articles + total） */
public class RecommendResponse {

    private List<ArticleListResponse.Card> articles;
    private long total;

    public List<ArticleListResponse.Card> getArticles() { return articles; }
    public void setArticles(List<ArticleListResponse.Card> articles) { this.articles = articles; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}

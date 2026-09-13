package com.yaozhidao.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/** 文章列表（分类列表/推荐共用卡片结构） */
public class ArticleListResponse {

    private List<Card> articles;
    private long total;
    private Integer page;
    private Integer size;
    private Integer totalPages;

    /** 文章卡片 */
    public static class Card {
        private Long articleId;
        private String title;
        private String summary;
        private String coverImage;
        private String category; // 中药/西药/养生/疾病/饮食
        private String author;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime publishTime;
        private Integer readCount;
        private Integer likeCount;
        private Boolean isLiked;
        private Boolean isCollected;

        public Long getArticleId() { return articleId; }
        public void setArticleId(Long articleId) { this.articleId = articleId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public String getCoverImage() { return coverImage; }
        public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public LocalDateTime getPublishTime() { return publishTime; }
        public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
        public Integer getReadCount() { return readCount; }
        public void setReadCount(Integer readCount) { this.readCount = readCount; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Boolean getIsLiked() { return isLiked; }
        public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
        public Boolean getIsCollected() { return isCollected; }
        public void setIsCollected(Boolean isCollected) { this.isCollected = isCollected; }
    }

    public List<Card> getArticles() { return articles; }
    public void setArticles(List<Card> articles) { this.articles = articles; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}

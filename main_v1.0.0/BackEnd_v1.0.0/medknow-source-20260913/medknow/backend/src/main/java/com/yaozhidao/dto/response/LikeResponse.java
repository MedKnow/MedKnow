package com.yaozhidao.dto.response;

/** 点赞切换响应 */
public class LikeResponse {

    private Boolean isLiked;
    private Integer likeCount;

    public LikeResponse() {
    }

    public LikeResponse(Boolean isLiked, Integer likeCount) {
        this.isLiked = isLiked;
        this.likeCount = likeCount;
    }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
}

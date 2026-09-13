package com.yaozhidao.dto.response;

/** 收藏切换响应 */
public class CollectResponse {

    private Boolean isCollected;

    public CollectResponse() {
    }

    public CollectResponse(Boolean isCollected) {
        this.isCollected = isCollected;
    }

    public Boolean getIsCollected() { return isCollected; }
    public void setIsCollected(Boolean isCollected) { this.isCollected = isCollected; }
}

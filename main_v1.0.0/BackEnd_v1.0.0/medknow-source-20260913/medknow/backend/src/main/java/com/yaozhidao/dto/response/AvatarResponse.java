package com.yaozhidao.dto.response;

/** 头像上传响应 */
public class AvatarResponse {

    private String avatarUrl;

    public AvatarResponse(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}

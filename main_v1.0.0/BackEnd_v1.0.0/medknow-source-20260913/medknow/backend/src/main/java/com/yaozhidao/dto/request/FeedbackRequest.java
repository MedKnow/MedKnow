package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 用户反馈 */
public class FeedbackRequest {

    @NotBlank(message = "反馈内容不能为空")
    private String content;

    private String contact;
    private java.util.List<String> images; // 截图 URL，最多3张

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public java.util.List<String> getImages() { return images; }
    public void setImages(java.util.List<String> images) { this.images = images; }
}

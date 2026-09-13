package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 分享文章 */
public class ShareRequest {

    @NotBlank(message = "分享目标平台不能为空")
    private String target; // WECHAT/QQ/COPY_LINK

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

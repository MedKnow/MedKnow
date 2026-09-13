package com.yaozhidao.dto.response;

/**
 * 发送验证码响应。
 * devCode 仅开发模式返回（application-dev.yml 的 app.dev-code-return=true），Android 端忽略未知字段
 */
public class SendCodeResponse {

    private int expireSeconds;

    /** 开发模式专用：明文验证码 */
    private String devCode;

    public SendCodeResponse(int expireSeconds, String devCode) {
        this.expireSeconds = expireSeconds;
        this.devCode = devCode;
    }

    public int getExpireSeconds() { return expireSeconds; }
    public void setExpireSeconds(int expireSeconds) { this.expireSeconds = expireSeconds; }
    public String getDevCode() { return devCode; }
    public void setDevCode(String devCode) { this.devCode = devCode; }
}

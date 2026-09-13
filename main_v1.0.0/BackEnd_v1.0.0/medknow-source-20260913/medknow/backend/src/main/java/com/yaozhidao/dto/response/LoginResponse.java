package com.yaozhidao.dto.response;

/** 登录成功响应 */
public class LoginResponse {

    private String token;
    private Long userId;
    private String userName;
    private String status;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long userId, String userName, String status) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.status = status;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

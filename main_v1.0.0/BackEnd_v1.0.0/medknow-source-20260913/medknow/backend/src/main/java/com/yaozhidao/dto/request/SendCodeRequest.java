package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 发送短信验证码 */
public class SendCodeRequest {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

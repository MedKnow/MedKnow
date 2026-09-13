package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** 服药打卡 */
public class CheckinRequest {

    @NotNull(message = "reminderId 不能为空")
    private Long reminderId;

    @NotBlank(message = "实际服药时间不能为空")
    private String actualTime; // yyyy-MM-dd HH:mm

    @NotNull(message = "isLate 不能为空")
    private Boolean isLate;

    public Long getReminderId() { return reminderId; }
    public void setReminderId(Long reminderId) { this.reminderId = reminderId; }
    public String getActualTime() { return actualTime; }
    public void setActualTime(String actualTime) { this.actualTime = actualTime; }
    public Boolean getIsLate() { return isLate; }
    public void setIsLate(Boolean isLate) { this.isLate = isLate; }
}

package com.yaozhidao.dto.response;

/** 用户偏好设置 */
public class SettingsResponse {

    private String themeColor;          // PURPLE/GREEN/BLUE/ORANGE/GRAY
    private String avatar;
    private Boolean notificationEnabled;
    private Boolean smsReminderEnabled;

    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    public Boolean getSmsReminderEnabled() { return smsReminderEnabled; }
    public void setSmsReminderEnabled(Boolean smsReminderEnabled) { this.smsReminderEnabled = smsReminderEnabled; }
}

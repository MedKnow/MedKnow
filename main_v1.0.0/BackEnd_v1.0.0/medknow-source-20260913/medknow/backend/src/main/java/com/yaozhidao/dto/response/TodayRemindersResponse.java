package com.yaozhidao.dto.response;

import java.util.List;

/** 今日待打卡列表 */
public class TodayRemindersResponse {

    private List<Item> reminders;

    public static class Item {
        private Long reminderId;
        private Long planId;
        private String drugName;
        private String scheduledTime; // 仅当天时间点 HH:mm
        private String status;

        public Long getReminderId() { return reminderId; }
        public void setReminderId(Long reminderId) { this.reminderId = reminderId; }
        public Long getPlanId() { return planId; }
        public void setPlanId(Long planId) { this.planId = planId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public String getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public List<Item> getReminders() { return reminders; }
    public void setReminders(List<Item> reminders) { this.reminders = reminders; }
}

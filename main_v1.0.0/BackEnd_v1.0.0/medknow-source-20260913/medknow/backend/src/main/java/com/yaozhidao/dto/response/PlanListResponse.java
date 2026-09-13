package com.yaozhidao.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/** 用药计划列表 */
public class PlanListResponse {

    private long total;
    private int pageNum;
    private int pageSize;
    private List<ListItem> plans;

    public static class ListItem {
        private Long planId;
        private String status;
        private Integer drugCount;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer adherenceRate;   // 百分比
        private String notes;
        private String mainDrugName;    // 如 "阿莫西林等2种"
        private List<String> reminderMethods;

        public Long getPlanId() { return planId; }
        public void setPlanId(Long planId) { this.planId = planId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Integer getDrugCount() { return drugCount; }
        public void setDrugCount(Integer drugCount) { this.drugCount = drugCount; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public Integer getAdherenceRate() { return adherenceRate; }
        public void setAdherenceRate(Integer adherenceRate) { this.adherenceRate = adherenceRate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public String getMainDrugName() { return mainDrugName; }
        public void setMainDrugName(String mainDrugName) { this.mainDrugName = mainDrugName; }
        public List<String> getReminderMethods() { return reminderMethods; }
        public void setReminderMethods(List<String> reminderMethods) { this.reminderMethods = reminderMethods; }
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPageNum() { return pageNum; }
    public void setPageNum(int pageNum) { this.pageNum = pageNum; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public List<ListItem> getPlans() { return plans; }
    public void setPlans(List<ListItem> plans) { this.plans = plans; }
}

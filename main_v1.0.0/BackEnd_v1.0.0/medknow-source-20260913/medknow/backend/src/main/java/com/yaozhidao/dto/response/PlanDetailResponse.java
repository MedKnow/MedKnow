package com.yaozhidao.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 用药计划详情 */
public class PlanDetailResponse {

    private Long planId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private Integer adherenceRate;
    private String diagnosis;
    private List<String> reminderMethods;
    private List<DrugItem> drugs;
    private List<ConflictItem> conflicts;
    private List<TodayReminderItem> todayReminders;

    /** 计划内药品（详细约束信息） */
    public static class DrugItem {
        private Long drugId;
        private String drugName;
        private String dosage;
        private String frequency;
        private String takeTime;
        private String takeMethod;
        private String dietaryRestrictions;
        private Boolean verified;
        private Boolean dosageRisk;

        public Long getDrugId() { return drugId; }
        public void setDrugId(Long drugId) { this.drugId = drugId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public String getTakeTime() { return takeTime; }
        public void setTakeTime(String takeTime) { this.takeTime = takeTime; }
        public String getTakeMethod() { return takeMethod; }
        public void setTakeMethod(String takeMethod) { this.takeMethod = takeMethod; }
        public String getDietaryRestrictions() { return dietaryRestrictions; }
        public void setDietaryRestrictions(String dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
        public Boolean getDosageRisk() { return dosageRisk; }
        public void setDosageRisk(Boolean dosageRisk) { this.dosageRisk = dosageRisk; }
    }

    /** 今日提醒 */
    public static class TodayReminderItem {
        private Long reminderId;
        private String drugName;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime scheduledTime;
        private String status;

        public Long getReminderId() { return reminderId; }
        public void setReminderId(Long reminderId) { this.reminderId = reminderId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Integer getAdherenceRate() { return adherenceRate; }
    public void setAdherenceRate(Integer adherenceRate) { this.adherenceRate = adherenceRate; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public List<String> getReminderMethods() { return reminderMethods; }
    public void setReminderMethods(List<String> reminderMethods) { this.reminderMethods = reminderMethods; }
    public List<DrugItem> getDrugs() { return drugs; }
    public void setDrugs(List<DrugItem> drugs) { this.drugs = drugs; }
    public List<ConflictItem> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictItem> conflicts) { this.conflicts = conflicts; }
    public List<TodayReminderItem> getTodayReminders() { return todayReminders; }
    public void setTodayReminders(List<TodayReminderItem> todayReminders) { this.todayReminders = todayReminders; }
}

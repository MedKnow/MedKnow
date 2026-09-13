package com.yaozhidao.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/** 创建用药计划响应 */
public class PlanCreateResponse {

    private Long planId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private String diagnosis;
    private List<String> reminderMethods;
    private List<DrugResult> drugs;
    private List<ConflictItem> conflicts;

    /** 服务端校验后的药品结果 */
    public static class DrugResult {
        private Long drugId;
        private String drugName;
        private Boolean verified;   // 是否匹配药品库
        private Boolean dosageRisk; // 剂量风险标记

        public DrugResult() {
        }

        public DrugResult(Long drugId, String drugName, Boolean verified, Boolean dosageRisk) {
            this.drugId = drugId;
            this.drugName = drugName;
            this.verified = verified;
            this.dosageRisk = dosageRisk;
        }

        public Long getDrugId() { return drugId; }
        public void setDrugId(Long drugId) { this.drugId = drugId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
        public Boolean getDosageRisk() { return dosageRisk; }
        public void setDosageRisk(Boolean dosageRisk) { this.dosageRisk = dosageRisk; }
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
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public List<String> getReminderMethods() { return reminderMethods; }
    public void setReminderMethods(List<String> reminderMethods) { this.reminderMethods = reminderMethods; }
    public List<DrugResult> getDrugs() { return drugs; }
    public void setDrugs(List<DrugResult> drugs) { this.drugs = drugs; }
    public List<ConflictItem> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictItem> conflicts) { this.conflicts = conflicts; }
}

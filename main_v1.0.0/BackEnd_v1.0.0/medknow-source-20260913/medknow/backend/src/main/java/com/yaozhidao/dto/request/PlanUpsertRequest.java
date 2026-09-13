package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** 创建/更新用药计划请求 */
public class PlanUpsertRequest {

    @NotEmpty(message = "药品清单不能为空")
    private List<PlanDrugRequest> drugs;

    @NotBlank(message = "开始日期不能为空")
    private String startDate; // yyyy-MM-dd，≥当天

    @NotBlank(message = "结束日期不能为空")
    private String endDate;   // yyyy-MM-dd，> startDate

    private String notes;
    private String diagnosis;

    @NotEmpty(message = "请至少选择一种提醒方式")
    private List<String> reminderMethods; // ALARM/PUSH/SMS

    public List<PlanDrugRequest> getDrugs() { return drugs; }
    public void setDrugs(List<PlanDrugRequest> drugs) { this.drugs = drugs; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public List<String> getReminderMethods() { return reminderMethods; }
    public void setReminderMethods(List<String> reminderMethods) { this.reminderMethods = reminderMethods; }
}

package com.yaozhidao.entity;

/** 计划内药品 */
public class PlanDrug {

    private Long id;
    private Long planId;
    private Long drugId;        // 未匹配药品库时为 null
    private String drugName;    // 用户输入原文
    private String dosage;      // 如 "1片"
    private String frequency;   // 如 "每日3次"
    private String takeTime;    // 逗号分隔 07:00,12:00,18:00
    private String takeMethod;  // BEFORE_MEAL/AFTER_MEAL/EMPTY_STOMACH/BEFORE_SLEEP
    private String dietaryRestrictions;
    private Boolean verified;   // 是否匹配药品库
    private Boolean dosageRisk; // 剂量是否超说明书
    private Integer sortNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
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
    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
}

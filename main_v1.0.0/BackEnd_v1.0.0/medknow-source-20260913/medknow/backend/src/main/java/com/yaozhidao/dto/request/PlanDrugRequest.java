package com.yaozhidao.dto.request;

import jakarta.validation.constraints.NotBlank;

/** 计划内药品 */
public class PlanDrugRequest {

    @NotBlank(message = "药品名称不能为空")
    private String drugName;

    @NotBlank(message = "剂量不能为空")
    private String dosage;      // 如 "1片"

    @NotBlank(message = "服用频次不能为空")
    private String frequency;   // 如 "每日3次"

    @NotBlank(message = "服药时间不能为空")
    private String takeTime;    // 逗号分隔 07:00,12:00,18:00

    @NotBlank(message = "服用方式不能为空")
    private String takeMethod;  // BEFORE_MEAL/AFTER_MEAL/EMPTY_STOMACH/BEFORE_SLEEP

    private String dietaryRestrictions;

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
}

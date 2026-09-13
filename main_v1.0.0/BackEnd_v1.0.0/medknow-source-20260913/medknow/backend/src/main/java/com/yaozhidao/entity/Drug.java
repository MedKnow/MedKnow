package com.yaozhidao.entity;

/** 药品库 */
public class Drug {

    private Long id;
    private String drugName;    // 商品名
    private String genericName; // 通用名
    private String category;
    private String summary;
    private String indications;
    private String dosage;
    private String sideEffects;
    private String contraindications;
    private String precautions;
    private String storage;
    private java.math.BigDecimal maxSingleDose; // 说明书最大单次剂量数值
    private String maxDoseUnit;                 // 对应单位

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getIndications() { return indications; }
    public void setIndications(String indications) { this.indications = indications; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getSideEffects() { return sideEffects; }
    public void setSideEffects(String sideEffects) { this.sideEffects = sideEffects; }
    public String getContraindications() { return contraindications; }
    public void setContraindications(String contraindications) { this.contraindications = contraindications; }
    public String getPrecautions() { return precautions; }
    public void setPrecautions(String precautions) { this.precautions = precautions; }
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    public java.math.BigDecimal getMaxSingleDose() { return maxSingleDose; }
    public void setMaxSingleDose(java.math.BigDecimal maxSingleDose) { this.maxSingleDose = maxSingleDose; }
    public String getMaxDoseUnit() { return maxDoseUnit; }
    public void setMaxDoseUnit(String maxDoseUnit) { this.maxDoseUnit = maxDoseUnit; }
}

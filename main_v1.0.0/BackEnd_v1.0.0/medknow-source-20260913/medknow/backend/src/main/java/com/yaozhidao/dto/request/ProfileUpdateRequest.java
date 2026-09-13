package com.yaozhidao.dto.request;

/** 更新个人信息（全部选填，但为空字段不更新） */
public class ProfileUpdateRequest {

    private String userName;      // 2-20 字符
    private Integer age;          // 0-120
    private String gender;        // MALE/FEMALE/OTHER
    private String occupation;
    private String allergies;
    private String chronicDiseases;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
}

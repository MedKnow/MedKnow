package com.yaozhidao.dto.response;

import java.util.List;

/** 医院详情 */
public class HospitalDetailResponse {

    private Long hospitalId;
    private String name;
    private String address;
    private String phone;
    private Double rating;
    private String introduction;
    private List<DepartmentItem> departments;

    public static class DepartmentItem {
        private String departmentName;
        private String description;

        public DepartmentItem() {
        }

        public DepartmentItem(String departmentName, String description) {
            this.departmentName = departmentName;
            this.description = description;
        }

        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public Long getHospitalId() { return hospitalId; }
    public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public List<DepartmentItem> getDepartments() { return departments; }
    public void setDepartments(List<DepartmentItem> departments) { this.departments = departments; }
}

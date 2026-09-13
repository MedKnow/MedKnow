package com.yaozhidao.dto.response;

import java.util.List;

/** 医院列表（推荐/搜索共用） */
public class HospitalListResponse {

    private long total;
    private int page;
    private int size;
    private List<ListItem> list;

    public static class ListItem {
        private Long hospitalId;
        private String name;
        private String address;
        private Double distance;          // 公里，保留一位小数（搜索接口无距离则省略）
        private Double rating;
        private List<String> mainDepartments; // 强项科室
        private Integer estimatedTime;    // 预计到达分钟（推荐接口才有）
        private String phone;

        public Long getHospitalId() { return hospitalId; }
        public void setHospitalId(Long hospitalId) { this.hospitalId = hospitalId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public List<String> getMainDepartments() { return mainDepartments; }
        public void setMainDepartments(List<String> mainDepartments) { this.mainDepartments = mainDepartments; }
        public Integer getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(Integer estimatedTime) { this.estimatedTime = estimatedTime; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public List<ListItem> getList() { return list; }
    public void setList(List<ListItem> list) { this.list = list; }
}

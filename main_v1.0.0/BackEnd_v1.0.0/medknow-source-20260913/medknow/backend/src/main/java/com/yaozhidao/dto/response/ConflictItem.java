package com.yaozhidao.dto.response;

import java.util.List;

/** 智能解析冲突项（INTERVAL 时间间隔 / CONTRAINDICATION 配伍禁忌） */
public class ConflictItem {

    private List<String> drugs;
    private String type;
    private String message;

    public ConflictItem() {
    }

    public ConflictItem(List<String> drugs, String type, String message) {
        this.drugs = drugs;
        this.type = type;
        this.message = message;
    }

    public List<String> getDrugs() { return drugs; }
    public void setDrugs(List<String> drugs) { this.drugs = drugs; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

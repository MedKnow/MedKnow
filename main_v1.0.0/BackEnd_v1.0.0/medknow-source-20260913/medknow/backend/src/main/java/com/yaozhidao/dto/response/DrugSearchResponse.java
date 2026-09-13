package com.yaozhidao.dto.response;

import java.util.List;

/** 药品搜索结果 */
public class DrugSearchResponse {

    private long total;
    private int page;
    private int size;
    private List<SearchItem> results;

    public static class SearchItem {
        private Long drugId;
        private String drugName;
        private String genericName;
        private String category;
        private String summary;

        public SearchItem() {
        }

        public SearchItem(Long drugId, String drugName, String genericName, String category, String summary) {
            this.drugId = drugId;
            this.drugName = drugName;
            this.genericName = genericName;
            this.category = category;
            this.summary = summary;
        }

        public Long getDrugId() { return drugId; }
        public void setDrugId(Long drugId) { this.drugId = drugId; }
        public String getDrugName() { return drugName; }
        public void setDrugName(String drugName) { this.drugName = drugName; }
        public String getGenericName() { return genericName; }
        public void setGenericName(String genericName) { this.genericName = genericName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public List<SearchItem> getResults() { return results; }
    public void setResults(List<SearchItem> results) { this.results = results; }
}

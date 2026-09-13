package com.yaozhidao.dto.response;

/** 依从率统计 */
public class StatsResponse {

    private String period;         // WEEK/MONTH
    private long totalDoses;       // 计划总次数
    private long confirmedDoses;   // 已确认（打卡）次数
    private long missedDoses;      // 漏服次数（EXPIRED）
    private Double adherenceRate;  // 依从率百分比，保留一位小数

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public long getTotalDoses() { return totalDoses; }
    public void setTotalDoses(long totalDoses) { this.totalDoses = totalDoses; }
    public long getConfirmedDoses() { return confirmedDoses; }
    public void setConfirmedDoses(long confirmedDoses) { this.confirmedDoses = confirmedDoses; }
    public long getMissedDoses() { return missedDoses; }
    public void setMissedDoses(long missedDoses) { this.missedDoses = missedDoses; }
    public Double getAdherenceRate() { return adherenceRate; }
    public void setAdherenceRate(Double adherenceRate) { this.adherenceRate = adherenceRate; }
}

package com.boyitong.dto;

import java.util.List;
import java.util.Map;

public class StatsVO {
    private long totalCustomers;
    private long cityCount;
    private long categoryCount;
    private long salespersonCount;
    private List<Map<String, Object>> cityDistribution;
    private List<Map<String, Object>> categoryDistribution;
    private List<Map<String, Object>> salespersonRanking;
    private List<Map<String, Object>> areaDistribution;

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public long getCityCount() { return cityCount; }
    public void setCityCount(long cityCount) { this.cityCount = cityCount; }

    public long getCategoryCount() { return categoryCount; }
    public void setCategoryCount(long categoryCount) { this.categoryCount = categoryCount; }

    public long getSalespersonCount() { return salespersonCount; }
    public void setSalespersonCount(long salespersonCount) { this.salespersonCount = salespersonCount; }

    public List<Map<String, Object>> getCityDistribution() { return cityDistribution; }
    public void setCityDistribution(List<Map<String, Object>> cityDistribution) { this.cityDistribution = cityDistribution; }

    public List<Map<String, Object>> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(List<Map<String, Object>> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public List<Map<String, Object>> getSalespersonRanking() { return salespersonRanking; }
    public void setSalespersonRanking(List<Map<String, Object>> salespersonRanking) { this.salespersonRanking = salespersonRanking; }

    public List<Map<String, Object>> getAreaDistribution() { return areaDistribution; }
    public void setAreaDistribution(List<Map<String, Object>> areaDistribution) { this.areaDistribution = areaDistribution; }
}
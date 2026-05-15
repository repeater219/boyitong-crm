package com.boyitong.dto;

public class CustomerQueryDTO {
    private String city;
    private String area;
    private String category;
    private Double minSize;
    private Double maxSize;
    private String salesperson;
    private String keyword;
    private int page = 0;
    private int size = 20;
    private String sortBy = "id";
    private String sortDir = "asc";

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMinSize() { return minSize; }
    public void setMinSize(Double minSize) { this.minSize = minSize; }

    public Double getMaxSize() { return maxSize; }
    public void setMaxSize(Double maxSize) { this.maxSize = maxSize; }

    public String getSalesperson() { return salesperson; }
    public void setSalesperson(String salesperson) { this.salesperson = salesperson; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}
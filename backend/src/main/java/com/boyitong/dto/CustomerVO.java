package com.boyitong.dto;

import com.boyitong.entity.Customer;

public class CustomerVO {
    private Long id;
    private String city;
    private String date;
    private String area;
    private String address;
    private String category;
    private Double size;
    private String phone;
    private String expiryDate;
    private String salesperson;
    private String remarks;
    private String accountName;
    private String status;
    private String assignedTo;

    public static CustomerVO fromEntity(Customer c) {
        CustomerVO vo = new CustomerVO();
        vo.setId(c.getId());
        vo.setCity(c.getCity());
        vo.setDate(c.getDate());
        vo.setArea(c.getArea());
        vo.setAddress(c.getAddress());
        vo.setCategory(c.getCategory());
        vo.setSize(c.getSize());
        vo.setPhone(c.getPhone());
        vo.setExpiryDate(c.getExpiryDate());
        vo.setSalesperson(c.getSalesperson());
        vo.setRemarks(c.getRemarks());
        vo.setAccountName(c.getAccountName());
        vo.setStatus(c.getStatus());
        vo.setAssignedTo(c.getAssignedTo());
        return vo;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getSize() { return size; }
    public void setSize(Double size) { this.size = size; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getSalesperson() { return salesperson; }
    public void setSalesperson(String salesperson) { this.salesperson = salesperson; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
}
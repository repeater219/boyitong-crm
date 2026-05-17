package com.boyitong.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 城市：柳州 / 鄂尔多斯 */
    @Column(name = "city")
    private String city;

    /** 日期 */
    @Column(name = "record_date")
    private String date;

    /** 区域 */
    @Column(name = "area")
    private String area;

    /** 地址 / 描述 */
    @Column(name = "address", length = 500)
    private String address;

    /** 行业/板块 */
    @Column(name = "category")
    private String category;

    /** 面积 */
    @Column(name = "size")
    private Double size;

    /** 电话 */
    @Column(name = "phone")
    private String phone;

    /** 到期日期 */
    @Column(name = "expiry_date")
    private String expiryDate;

    /** 销售名 */
    @Column(name = "salesperson")
    private String salesperson;

    /** 备注 */
    @Column(name = "remarks", length = 500)
    private String remarks;

    /** 账号名（鄂尔多斯特有） */
    @Column(name = "account_name")
    private String accountName;

    /** 客户状态: NEW / FOLLOWING / NEGOTIATING / WON / LOST */
    @Column(name = "status")
    private String status = "NEW";

    /** 分配给哪个销售员（用户名） */
    @Column(name = "assigned_to")
    private String assignedTo;

    /** 分配给哪个销售员（用户ID） */
    @Column(name = "assigned_to_user_id")
    private Long assignedToUserId;

    public Customer() {}

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

    public Long getAssignedToUserId() { return assignedToUserId; }
    public void setAssignedToUserId(Long assignedToUserId) { this.assignedToUserId = assignedToUserId; }
}
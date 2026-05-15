package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long contractId;
    private Double amount;
    private LocalDate planDate;
    private LocalDate actualDate;
    private String status; // PENDING / PAID
    private String remark;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getContractId() { return contractId; } public void setContractId(Long contractId) { this.contractId = contractId; }
    public Double getAmount() { return amount; } public void setAmount(Double amount) { this.amount = amount; }
    public LocalDate getPlanDate() { return planDate; } public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }
    public LocalDate getActualDate() { return actualDate; } public void setActualDate(LocalDate actualDate) { this.actualDate = actualDate; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; } public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
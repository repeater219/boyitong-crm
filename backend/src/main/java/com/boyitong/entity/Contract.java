package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
public class Contract {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contractNo;
    private String name;
    private Long customerId;
    private Long opportunityId;
    private Double amount;
    private String status; // DRAFT / ACTIVE / COMPLETED / TERMINATED
    private LocalDate startDate;
    private LocalDate endDate;
    private String salesperson;
    @Column(name = "salesperson_user_id")
    private Long salespersonUserId;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getContractNo() { return contractNo; } public void setContractNo(String contractNo) { this.contractNo = contractNo; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getOpportunityId() { return opportunityId; } public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }
    public Double getAmount() { return amount; } public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getSalesperson() { return salesperson; } public void setSalesperson(String salesperson) { this.salesperson = salesperson; }
    public Long getSalespersonUserId() { return salespersonUserId; } public void setSalespersonUserId(Long salespersonUserId) { this.salespersonUserId = salespersonUserId; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
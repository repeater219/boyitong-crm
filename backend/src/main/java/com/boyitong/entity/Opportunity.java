package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "opportunities")
public class Opportunity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long customerId;
    private Double amount;
    private String stage; // INTENT / PROPOSAL / QUOTATION / NEGOTIATION / WON / LOST
    private Double winRate;
    private String salesperson;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Long getCustomerId() { return customerId; } public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Double getAmount() { return amount; } public void setAmount(Double amount) { this.amount = amount; }
    public String getStage() { return stage; } public void setStage(String stage) { this.stage = stage; }
    public Double getWinRate() { return winRate; } public void setWinRate(Double winRate) { this.winRate = winRate; }
    public String getSalesperson() { return salesperson; } public void setSalesperson(String salesperson) { this.salesperson = salesperson; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
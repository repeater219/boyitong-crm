package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follow_ups")
public class FollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "salesperson", nullable = false)
    private String salesperson;

    /** 跟进方式: PHONE / VISIT / CHAT / OTHER */
    @Column(name = "method")
    private String method;

    @Column(name = "content", length = 2000, nullable = false)
    private String content;

    /** 下次跟进日期 */
    @Column(name = "next_follow_date")
    private String nextFollowDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSalesperson() { return salesperson; }
    public void setSalesperson(String salesperson) { this.salesperson = salesperson; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getNextFollowDate() { return nextFollowDate; }
    public void setNextFollowDate(String nextFollowDate) { this.nextFollowDate = nextFollowDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
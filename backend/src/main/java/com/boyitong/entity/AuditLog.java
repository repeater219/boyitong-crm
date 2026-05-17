package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_id")
    private String targetId;

    @Column(name = "detail", length = 1000)
    private String detail;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(String username, String action, String targetType, String targetId, String detail) {
        this.username = username;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
    }

    public AuditLog(String username, Long userId, String action, String targetType, String targetId, String detail) {
        this.username = username;
        this.userId = userId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAction() { return action; }
    public String getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public String getDetail() { return detail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
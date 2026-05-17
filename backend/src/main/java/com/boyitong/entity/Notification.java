package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 接收者用户名（ADMIN 表示全体管理员） */
    @Column(name = "recipient", nullable = false)
    private String recipient;

    /** 接收者用户ID（null 表示全体管理员通知） */
    @Column(name = "recipient_user_id")
    private Long recipientUserId;

    /** 标题 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 内容 */
    @Column(name = "content", length = 1000)
    private String content;

    /** 是否已读 */
    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(String recipient, String title, String content) {
        this.recipient = recipient;
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public Long getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(Long recipientUserId) { this.recipientUserId = recipientUserId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
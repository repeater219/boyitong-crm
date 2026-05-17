package com.boyitong.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upload_records")
public class UploadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 上传者用户名 */
    @Column(name = "uploader", nullable = false)
    private String uploader;

    /** 上传者用户ID */
    @Column(name = "uploader_user_id")
    private Long uploaderUserId;

    /** 城市 */
    @Column(name = "city")
    private String city;

    /** 文件名 */
    @Column(name = "file_name")
    private String fileName;

    /** Excel 文件内容 (BLOB) */
    @Column(name = "file_data", columnDefinition = "BYTEA")
    private byte[] fileData;

    /** 导入的记录数 */
    @Column(name = "record_count")
    private int recordCount;

    /** 状态: PENDING / APPROVED / REJECTED */
    @Column(name = "status", nullable = false)
    private String status = "PENDING";

    /** 审核者 */
    @Column(name = "reviewer")
    private String reviewer;

    /** 审核者用户ID */
    @Column(name = "reviewer_user_id")
    private Long reviewerUserId;

    /** 审核备注 */
    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    /** 上传时间 */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 审核时间 */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUploader() { return uploader; }
    public void setUploader(String uploader) { this.uploader = uploader; }

    public Long getUploaderUserId() { return uploaderUserId; }
    public void setUploaderUserId(Long uploaderUserId) { this.uploaderUserId = uploaderUserId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) { this.recordCount = recordCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public Long getReviewerUserId() { return reviewerUserId; }
    public void setReviewerUserId(Long reviewerUserId) { this.reviewerUserId = reviewerUserId; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
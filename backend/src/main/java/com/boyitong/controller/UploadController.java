package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.Customer;
import com.boyitong.entity.UploadRecord;
import com.boyitong.repository.CustomerRepository;
import com.boyitong.repository.UploadRecordRepository;
import com.boyitong.service.AuditLogService;
import com.boyitong.service.ImportService;
import com.boyitong.service.NotificationService;
import com.boyitong.service.UserResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final ImportService importService;
    private final CustomerRepository customerRepository;
    private final UploadRecordRepository uploadRecordRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final UserResolver userResolver;

    public UploadController(ImportService importService,
                            CustomerRepository customerRepository,
                            UploadRecordRepository uploadRecordRepository,
                            AuditLogService auditLogService,
                            NotificationService notificationService,
                            UserResolver userResolver) {
        this.importService = importService;
        this.customerRepository = customerRepository;
        this.uploadRecordRepository = uploadRecordRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
        this.userResolver = userResolver;
    }

    /** 业务员上传Excel，进入待审核状态 */
    @PostMapping("/submit")
    public Result<String> submitUpload(@RequestParam("file") MultipartFile file,
                                       @RequestParam("city") String city,
                                       Authentication auth) throws IOException {
        String uploader = auth.getName();
        List<Customer> parsed = importService.parseExcel(file, city);

        UploadRecord record = new UploadRecord();
        record.setUploader(uploader);
        record.setUploaderUserId(userResolver.getUserId(uploader));
        record.setCity(city);
        record.setFileName(file.getOriginalFilename());
        record.setFileData(file.getBytes());
        record.setRecordCount(parsed.size());
        record.setStatus("PENDING");
        uploadRecordRepository.save(record);

        auditLogService.log(uploader, "UPLOAD", "UploadRecord", String.valueOf(record.getId()),
                "上传 " + city + " 数据，共 " + parsed.size() + " 条");

        notificationService.create("ADMIN", "新数据待审核",
                uploader + " 上传了 " + city + " 的 " + parsed.size() + " 条数据，请审核");

        return Result.success("上传成功，共 " + parsed.size() + " 条记录，等待管理员审核");
    }

    /** 管理员审核通过 — 将数据并入主表 */
    @PostMapping("/{id}/approve")
    public Result<String> approve(@PathVariable Long id,
                                  @RequestBody(required = false) ApproveRequest request,
                                  Authentication auth) {
        UploadRecord record = uploadRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("上传记录不存在"));

        if (!"PENDING".equals(record.getStatus())) {
            return Result.error(400, "该记录已被审核");
        }

        List<Customer> customers = importService.parseExcel(record.getFileData(), record.getCity());
        customerRepository.saveAll(customers);

        record.setStatus("APPROVED");
        record.setReviewer(auth.getName());
        record.setReviewerUserId(userResolver.getUserId(auth.getName()));
        record.setReviewedAt(LocalDateTime.now());
        if (request != null) {
            record.setReviewComment(request.getComment());
        }
        uploadRecordRepository.save(record);

        auditLogService.log(auth.getName(), "APPROVE_UPLOAD", "UploadRecord", String.valueOf(record.getId()),
                "审核通过 " + record.getUploader() + " 上传的 " + record.getCity() + " 数据，共 " + customers.size() + " 条");

        notificationService.create(record.getUploader(), "审核通过",
                "你上传的 " + record.getCity() + " 数据已通过审核，共导入 " + customers.size() + " 条");

        return Result.success("审核通过，已导入 " + customers.size() + " 条数据");
    }

    /** 管理员审核拒绝 */
    @PostMapping("/{id}/reject")
    public Result<String> reject(@PathVariable Long id,
                                 @RequestBody(required = false) ApproveRequest request,
                                 Authentication auth) {
        UploadRecord record = uploadRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("上传记录不存在"));

        if (!"PENDING".equals(record.getStatus())) {
            return Result.error(400, "该记录已被审核");
        }

        record.setStatus("REJECTED");
        record.setReviewer(auth.getName());
        record.setReviewerUserId(userResolver.getUserId(auth.getName()));
        record.setReviewedAt(LocalDateTime.now());
        if (request != null) {
            record.setReviewComment(request.getComment());
        }
        uploadRecordRepository.save(record);

        auditLogService.log(auth.getName(), "REJECT_UPLOAD", "UploadRecord", String.valueOf(record.getId()),
                "拒绝 " + record.getUploader() + " 上传的 " + record.getCity() + " 数据" +
                        (request != null && request.getComment() != null ? "，原因：" + request.getComment() : ""));

        notificationService.create(record.getUploader(), "审核未通过",
                "你上传的 " + record.getCity() + " 数据未通过审核" +
                        (request != null && request.getComment() != null ? "，原因：" + request.getComment() : ""));

        return Result.success("已拒绝");
    }

    /** 查看自己的上传记录（业务员） */
    @GetMapping("/my")
    public Result<List<UploadRecord>> myUploads(Authentication auth) {
        return Result.success(uploadRecordRepository.findByUploaderOrderByCreatedAtDesc(auth.getName()));
    }

    /** 查看所有待审核记录（管理员） */
    @GetMapping("/pending")
    public Result<List<UploadRecord>> pendingUploads() {
        return Result.success(uploadRecordRepository.findByStatusOrderByCreatedAtDesc("PENDING"));
    }

    /** 查看所有记录（管理员） */
    @GetMapping("/all")
    public Result<List<UploadRecord>> allUploads() {
        return Result.success(uploadRecordRepository.findAllByOrderByCreatedAtDesc());
    }

    public static class ApproveRequest {
        private String comment;
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}
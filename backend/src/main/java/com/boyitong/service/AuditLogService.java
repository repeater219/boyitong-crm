package com.boyitong.service;

import com.boyitong.entity.AuditLog;
import com.boyitong.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String username, String action, String targetType, String targetId, String detail) {
        auditLogRepository.save(new AuditLog(username, action, targetType, targetId, detail));
    }

    public void log(String username, Long userId, String action, String targetType, String targetId, String detail) {
        auditLogRepository.save(new AuditLog(username, userId, action, targetType, targetId, detail));
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
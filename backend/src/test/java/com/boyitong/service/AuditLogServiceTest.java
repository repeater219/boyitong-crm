package com.boyitong.service;

import com.boyitong.entity.AuditLog;
import com.boyitong.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogService(auditLogRepository);
    }

    @Test
    void log_ShouldSaveWithCorrectFields() {
        auditLogService.log("zhangrui", "UPLOAD", "UploadRecord", "1", "上传柳州数据，共10条");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog saved = captor.getValue();
        assertEquals("zhangrui", saved.getUsername());
        assertEquals("UPLOAD", saved.getAction());
        assertEquals("UploadRecord", saved.getTargetType());
        assertEquals("1", saved.getTargetId());
        assertEquals("上传柳州数据，共10条", saved.getDetail());
    }

    @Test
    void log_ShouldHandleEmptyDetail() {
        auditLogService.log("admin", "DELETE", "Customer", "5", "");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertEquals("", captor.getValue().getDetail());
    }

    @Test
    void findAll_ShouldReturnAllLogsOrderedByDesc() {
        AuditLog log1 = new AuditLog("admin", "APPROVE_UPLOAD", "UploadRecord", "1", "审核通过");
        AuditLog log2 = new AuditLog("zhangrui", "UPLOAD", "UploadRecord", "2", "上传数据");
        when(auditLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(log1, log2));

        List<AuditLog> result = auditLogService.findAll();

        assertEquals(2, result.size());
        assertEquals("APPROVE_UPLOAD", result.get(0).getAction());
        verify(auditLogRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void findAll_WithNoLogs_ShouldReturnEmpty() {
        when(auditLogRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<AuditLog> result = auditLogService.findAll();

        assertTrue(result.isEmpty());
    }
}
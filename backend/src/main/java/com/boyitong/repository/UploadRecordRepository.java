package com.boyitong.repository;

import com.boyitong.entity.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {
    List<UploadRecord> findByUploaderOrderByCreatedAtDesc(String uploader);
    List<UploadRecord> findByStatusOrderByCreatedAtDesc(String status);
    List<UploadRecord> findAllByOrderByCreatedAtDesc();
}
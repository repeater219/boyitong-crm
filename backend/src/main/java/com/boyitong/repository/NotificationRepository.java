package com.boyitong.repository;

import com.boyitong.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
    List<Notification> findByRecipientAndReadFalse(String recipient);
    long countByRecipientAndReadFalse(String recipient);
}
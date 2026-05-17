package com.boyitong.service;

import com.boyitong.entity.Notification;
import com.boyitong.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void create(String recipient, String title, String content) {
        notificationRepository.save(new Notification(recipient, title, content));
    }

    public void create(String recipient, Long recipientUserId, String title, String content) {
        Notification n = new Notification(recipient, title, content);
        n.setRecipientUserId(recipientUserId);
        notificationRepository.save(n);
    }

    public List<Notification> getForUser(String username) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(username);
    }

    public List<Notification> getUnreadForUser(String username) {
        return notificationRepository.findByRecipientAndReadFalse(username);
    }

    public long countUnread(String username) {
        return notificationRepository.countByRecipientAndReadFalse(username);
    }

    public void markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead(String username) {
        List<Notification> unread = notificationRepository.findByRecipientAndReadFalse(username);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
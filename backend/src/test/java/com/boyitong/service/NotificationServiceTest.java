package com.boyitong.service;

import com.boyitong.entity.Notification;
import com.boyitong.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void create_ShouldSaveNotification() {
        notificationService.create("zhangrui", "审核通过", "你的数据已通过审核");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals("zhangrui", saved.getRecipient());
        assertEquals("审核通过", saved.getTitle());
        assertEquals("你的数据已通过审核", saved.getContent());
        assertFalse(saved.isRead());
    }

    @Test
    void getForUser_ShouldReturnUserNotifications() {
        Notification n1 = new Notification("zhangrui", "审核通过", "已通过");
        Notification n2 = new Notification("zhangrui", "审核未通过", "未通过");
        when(notificationRepository.findByRecipientOrderByCreatedAtDesc("zhangrui"))
                .thenReturn(List.of(n1, n2));

        List<Notification> result = notificationService.getForUser("zhangrui");

        assertEquals(2, result.size());
    }

    @Test
    void countUnread_ShouldReturnCorrectCount() {
        when(notificationRepository.countByRecipientAndReadFalse("zhangrui")).thenReturn(5L);

        long count = notificationService.countUnread("zhangrui");

        assertEquals(5L, count);
    }

    @Test
    void markAsRead_ShouldUpdateNotification() {
        Notification n = new Notification("zhangrui", "审核通过", "已通过");
        n.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L);

        assertTrue(n.isRead());
        verify(notificationRepository).save(n);
    }

    @Test
    void markAllAsRead_ShouldMarkAllUnread() {
        Notification n1 = new Notification("zhangrui", "通知1", "内容1");
        Notification n2 = new Notification("zhangrui", "通知2", "内容2");
        when(notificationRepository.findByRecipientAndReadFalse("zhangrui"))
                .thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead("zhangrui");

        assertTrue(n1.isRead());
        assertTrue(n2.isRead());
        verify(notificationRepository).saveAll(List.of(n1, n2));
    }

    @Test
    void getUnread_ShouldReturnOnlyUnread() {
        Notification n = new Notification("zhangrui", "待读", "内容");
        when(notificationRepository.findByRecipientAndReadFalse("zhangrui"))
                .thenReturn(List.of(n));

        List<Notification> result = notificationService.getUnreadForUser("zhangrui");

        assertEquals(1, result.size());
    }
}
package com.boyitong.controller;

import com.boyitong.common.Result;
import com.boyitong.entity.Notification;
import com.boyitong.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public Result<List<Notification>> getMyNotifications(Authentication auth) {
        return Result.success(notificationService.getForUser(auth.getName()));
    }

    @GetMapping("/unread")
    public Result<List<Notification>> getUnread(Authentication auth) {
        return Result.success(notificationService.getUnreadForUser(auth.getName()));
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(Authentication auth) {
        return Result.success(notificationService.countUnread(auth.getName()));
    }

    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PostMapping("/read-all")
    public Result<Void> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return Result.success();
    }
}
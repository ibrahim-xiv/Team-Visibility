package com.TeamVisibility.App.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeamVisibility.App.model.Notification;
import com.TeamVisibility.App.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService ns) { this.notificationService = ns; }

    @GetMapping
    public ResponseEntity<?> list(HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(notificationService.getForUser(uid));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount(HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(Map.of("count", notificationService.unreadCount(uid)));
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markRead(HttpSession session) {
        Long uid = (Long) session.getAttribute("userId");
        if (uid == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        notificationService.markAllRead(uid);
        return ResponseEntity.ok(Map.of("message", "OK"));
    }
}

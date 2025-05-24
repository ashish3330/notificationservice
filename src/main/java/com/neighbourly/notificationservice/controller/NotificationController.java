package com.neighbourly.notificationservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.notificationservice.dto.NotificationRequestDTO;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestBody NotificationRequestDTO dto,
            @RequestHeader("user-id") Long userId) {
        Either<String, NotificationResponseDTO> result = notificationService.createNotification(userId, dto);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable("id") Long notificationId) {
        Either<String, NotificationResponseDTO> result = notificationService.markNotificationAsRead(notificationId);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable("userId") Long userId) {
        Either<String, List<NotificationResponseDTO>> result = notificationService.getUserNotifications(userId);
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }
}

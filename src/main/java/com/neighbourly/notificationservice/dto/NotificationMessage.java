package com.neighbourly.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationMessage {
    private Long userId;
    private Long serviceId;
    private Long orderId;
    private Long typeId;
    private String content;
    private LocalDateTime timestamp;
}
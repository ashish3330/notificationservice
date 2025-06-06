package com.neighbourly.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequestDTO {

    @NotBlank(message = "Notification content is mandatory")
    private String content;

    @NotNull(message = "Notification type ID is mandatory")
    private Long typeId;

    private Long serviceId;

    private Long orderId;
}
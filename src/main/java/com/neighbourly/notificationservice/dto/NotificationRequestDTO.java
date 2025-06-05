package com.neighbourly.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequestDTO {

    // Getters and Setters
    @NotBlank(message = "Notification content is mandatory")
    private String content;

    @NotNull(message = "Notification type ID is mandatory")
    private Long typeId;

}

package com.neighbourly.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequestDTO {

    @NotBlank(message = "Notification content is mandatory")
    private String content;

    @NotNull(message = "Notification type ID is mandatory")
    private Long typeId;

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
}

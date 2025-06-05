package com.neighbourly.notificationservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import lombok.Getter;

@Getter
public class MarkNotificationAsReadCommand extends Command<NotificationResponseDTO> {
    private final Long notificationId;

    public MarkNotificationAsReadCommand(Long notificationId) {
        this.notificationId = notificationId;
    }

}

package com.neighbourly.notificationservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.notificationservice.dto.NotificationRequestDTO;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import lombok.Getter;

@Getter
public class CreateNotificationCommand extends Command<NotificationResponseDTO> {
    private final Long userId;
    private final NotificationRequestDTO requestDTO;

    public CreateNotificationCommand(Long userId, NotificationRequestDTO requestDTO) {
        this.userId = userId;
        this.requestDTO = requestDTO;
    }

}

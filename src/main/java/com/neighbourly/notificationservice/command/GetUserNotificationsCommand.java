package com.neighbourly.notificationservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class GetUserNotificationsCommand extends Command<List<NotificationResponseDTO>> {
    private final Long userId;

    public GetUserNotificationsCommand(Long userId) {
        this.userId = userId;
    }

}

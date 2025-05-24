package com.neighbourly.notificationservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;

import java.util.List;

public class GetUserNotificationsCommand extends Command<List<NotificationResponseDTO>> {
    private final Long userId;

    public GetUserNotificationsCommand(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
}

package com.neighbourly.notificationservice.service;


import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.commonservice.service.GenericService;
import com.neighbourly.notificationservice.command.CreateNotificationCommand;
import com.neighbourly.notificationservice.command.GetUserNotificationsCommand;
import com.neighbourly.notificationservice.command.MarkNotificationAsReadCommand;
import com.neighbourly.notificationservice.dto.NotificationRequestDTO;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService  {

    private final Dispatcher syncDispatcher;

    public NotificationService(Dispatcher syncDispatcher) {
        this.syncDispatcher = syncDispatcher;
    }

    public Either<String, NotificationResponseDTO> createNotification(Long userId, NotificationRequestDTO requestDTO) {
        return syncDispatcher.dispatch(new CreateNotificationCommand(userId, requestDTO));
    }

    public Either<String, NotificationResponseDTO> markNotificationAsRead(Long notificationId) {
        return syncDispatcher.dispatch(new MarkNotificationAsReadCommand(notificationId));
    }

    public Either<String, List<NotificationResponseDTO>> getUserNotifications(Long userId) {
        return syncDispatcher.dispatch(new GetUserNotificationsCommand(userId));
    }

}
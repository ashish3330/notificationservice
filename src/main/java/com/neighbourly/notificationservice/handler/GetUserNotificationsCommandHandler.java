package com.neighbourly.notificationservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.notificationservice.command.GetUserNotificationsCommand;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.entity.Notification;
import com.neighbourly.notificationservice.entity.NotificationType;
import com.neighbourly.notificationservice.exception.NotificationException;
import com.neighbourly.notificationservice.repository.NotificationRepository;
import com.neighbourly.notificationservice.repository.NotificationTypeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GetUserNotificationsCommandHandler implements CommandHandler<GetUserNotificationsCommand, List<NotificationResponseDTO>> {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public GetUserNotificationsCommandHandler(NotificationRepository notificationRepository,
                                              NotificationTypeRepository notificationTypeRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Override
    public Either<String, List<NotificationResponseDTO>> handle(GetUserNotificationsCommand command) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(command.getUserId());

            // Fetch all notification types in one query
            List<Long> typeIds = notifications.stream()
                    .map(Notification::getTypeId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, NotificationType> typeMap = notificationTypeRepository.findAllById(typeIds)
                    .stream()
                    .collect(Collectors.toMap(NotificationType::getTypeId, Function.identity()));

            List<NotificationResponseDTO> response = notifications.stream()
                    .map(notification -> {
                        NotificationType type = typeMap.get(notification.getTypeId());
                        if (type == null) {
                            throw new NotificationException("Notification type not found with ID: " + notification.getTypeId());
                        }
                        return mapToResponse(notification, type);
                    })
                    .collect(Collectors.toList());

            return Either.right(response);
        } catch (NotificationException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to fetch notifications: " + e.getMessage());
        }
    }

    private NotificationResponseDTO mapToResponse(Notification notification, NotificationType notificationType) {
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setNotificationId(notification.getNotificationId());
        response.setUserId(notification.getUserId());
        response.setServiceId(notification.getServiceId());
        response.setOrderId(notification.getOrderId());
        response.setTypeId(notification.getTypeId());
        response.setTypeName(notificationType.getTypeName());
        response.setContent(notification.getContent());
        response.setRead(notification.isRead());
        response.setInformational(notification.isInformational());
        response.setCreatedAt(notification.getCreatedAt());
        response.setModifiedAt(notification.getModifiedAt());
        return response;
    }
}
package com.neighbourly.notificationservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.notificationservice.command.CreateNotificationCommand;
import com.neighbourly.notificationservice.dto.NotificationRequestDTO;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.entity.Notification;
import com.neighbourly.notificationservice.entity.NotificationType;
import com.neighbourly.notificationservice.exception.NotificationException;
import com.neighbourly.notificationservice.repository.NotificationRepository;
import com.neighbourly.notificationservice.repository.NotificationTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateNotificationCommandHandler implements CommandHandler<CreateNotificationCommand, NotificationResponseDTO> {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public CreateNotificationCommandHandler(NotificationRepository notificationRepository,
                                            NotificationTypeRepository notificationTypeRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Override
    public Either<String, NotificationResponseDTO> handle(CreateNotificationCommand command) {
        try {
            Long userId = command.getUserId();
            if (userId == null) {
                return Either.left("User ID is required");
            }

            NotificationRequestDTO dto = command.getRequestDTO();
            if (dto.getContent() == null || dto.getContent().isEmpty()) {
                return Either.left("Notification content is required");
            }
            if (dto.getTypeId() == null) {
                return Either.left("Notification type ID is required");
            }

            // Validate notification type
            NotificationType notificationType = notificationTypeRepository.findById(dto.getTypeId())
                    .orElseThrow(() -> new NotificationException("Notification type not found with ID: " + dto.getTypeId()));

            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTypeId(dto.getTypeId());
            notification.setContent(dto.getContent());
            notification.setRead(false);

            notification = notificationRepository.save(notification);

            return Either.right(mapToResponse(notification, notificationType));
        } catch (NotificationException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to create notification: " + e.getMessage());
        }
    }

    private NotificationResponseDTO mapToResponse(Notification notification, NotificationType notificationType) {
        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setNotificationId(notification.getNotificationId());
        response.setUserId(notification.getUserId());
        response.setTypeId(notification.getTypeId());
        response.setTypeName(notificationType.getTypeName());
        response.setContent(notification.getContent());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
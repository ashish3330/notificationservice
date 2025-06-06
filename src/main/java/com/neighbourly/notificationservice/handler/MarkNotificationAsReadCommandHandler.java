package com.neighbourly.notificationservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.notificationservice.command.MarkNotificationAsReadCommand;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.entity.Notification;
import com.neighbourly.notificationservice.entity.NotificationType;
import com.neighbourly.notificationservice.exception.NotificationException;
import com.neighbourly.notificationservice.repository.NotificationRepository;
import com.neighbourly.notificationservice.repository.NotificationTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class MarkNotificationAsReadCommandHandler implements CommandHandler<MarkNotificationAsReadCommand, NotificationResponseDTO> {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;

    public MarkNotificationAsReadCommandHandler(NotificationRepository notificationRepository,
                                                NotificationTypeRepository notificationTypeRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    @Override
    public Either<String, NotificationResponseDTO> handle(MarkNotificationAsReadCommand command) {
        try {
            Notification notification = notificationRepository.findById(command.getNotificationId())
                    .orElseThrow(() -> new NotificationException("Notification not found with ID: " + command.getNotificationId()));

            if (notification.isRead()) {
                return Either.left("Notification is already marked as read");
            }

            Notification finalNotification = notification;
            NotificationType notificationType = notificationTypeRepository.findById(notification.getTypeId())
                    .orElseThrow(() -> new NotificationException("Notification type not found with ID: " + finalNotification.getTypeId()));

            notification.setRead(true);
            notification = notificationRepository.save(notification);

            return Either.right(mapToResponse(notification, notificationType));
        } catch (NotificationException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to mark notification as read: " + e.getMessage());
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
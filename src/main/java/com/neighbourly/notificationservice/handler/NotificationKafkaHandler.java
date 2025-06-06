package com.neighbourly.notificationservice.handler;

import com.neighbourly.notificationservice.dto.NotificationMessage;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.entity.Notification;
import com.neighbourly.notificationservice.entity.NotificationType;
import com.neighbourly.notificationservice.entity.UserDevice;
import com.neighbourly.notificationservice.exception.NotificationException;
import com.neighbourly.notificationservice.repository.NotificationRepository;
import com.neighbourly.notificationservice.repository.NotificationTypeRepository;
import com.neighbourly.notificationservice.repository.UserDeviceRepository;
import com.neighbourly.notificationservice.service.PushNotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationKafkaHandler {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PushNotificationService pushNotificationService;
    private final KafkaTemplate<String, NotificationResponseDTO> kafkaTemplate;

    public NotificationKafkaHandler(NotificationRepository notificationRepository,
                                    NotificationTypeRepository notificationTypeRepository,
                                    UserDeviceRepository userDeviceRepository,
                                    PushNotificationService pushNotificationService,
                                    KafkaTemplate<String, NotificationResponseDTO> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.pushNotificationService = pushNotificationService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "notification.create", groupId = "notification-group")
    public void consumeNotificationMessage(NotificationMessage message) {
        try {
            // Validate message
            if (message.getUserId() == null || message.getTypeId() == null ||
                    message.getContent() == null || message.getContent().isEmpty()) {
                throw new NotificationException("Invalid notification message: userId, typeId, and content are required");
            }

            // Validate notification type
            NotificationType notificationType = notificationTypeRepository.findById(message.getTypeId())
                    .orElseThrow(() -> new NotificationException("Notification type not found with ID: " + message.getTypeId()));

            // Create and save notification
            Notification notification = new Notification();
            notification.setUserId(message.getUserId());
            notification.setServiceId(message.getServiceId());
            notification.setOrderId(message.getOrderId());
            notification.setTypeId(message.getTypeId());
            notification.setContent(message.getContent());
            notification.setRead(false);
            notification.setInformational(message.getServiceId() == null && message.getOrderId() == null);

            notification = notificationRepository.save(notification);

            // Send push notifications to user's devices
            List<UserDevice> devices = userDeviceRepository.findByUserId(message.getUserId());
            for (UserDevice device : devices) {
                try {
                    pushNotificationService.sendPushNotification(device,
                            notificationType.getTypeName(), // Use typeName as title
                            notification.getContent());    // Use content as body
                } catch (NotificationException e) {
                    System.err.println("Failed to send push notification to device: " + device.getDeviceToken() + " - " + e.getMessage());
                }
            }

            // Produce response to notification.events topic
            NotificationResponseDTO response = mapToResponse(notification, notificationType);
            kafkaTemplate.send("notification.events", response);
        } catch (Exception e) {
            System.err.println("Failed to process notification message: " + e.getMessage());
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
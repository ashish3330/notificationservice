package com.neighbourly.notificationservice.handler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.notificationservice.command.CreateNotificationCommand;
import com.neighbourly.notificationservice.dto.NotificationRequestDTO;
import com.neighbourly.notificationservice.dto.NotificationResponseDTO;
import com.neighbourly.notificationservice.entity.UserDevice;
import com.neighbourly.notificationservice.entity.NotificationType;
import com.neighbourly.notificationservice.entity.Notification;
import com.neighbourly.notificationservice.exception.NotificationException;
import com.neighbourly.notificationservice.repository.UserDeviceRepository;
import com.neighbourly.notificationservice.repository.NotificationRepository;
import com.neighbourly.notificationservice.repository.NotificationTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateNotificationCommandHandler implements CommandHandler<CreateNotificationCommand, NotificationResponseDTO> {

    private static final Logger logger = LoggerFactory.getLogger(CreateNotificationCommandHandler.class);
    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CreateNotificationCommandHandler(NotificationRepository notificationRepository,
                                            NotificationTypeRepository notificationTypeRepository,
                                            UserDeviceRepository userDeviceRepository,
                                            FirebaseMessaging firebaseMessaging,
                                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.notificationTypeRepository = notificationTypeRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.firebaseMessaging = firebaseMessaging;
        this.kafkaTemplate = kafkaTemplate;
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

            // Save notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTypeId(dto.getTypeId());
            notification.setContent(dto.getContent());
            notification.setRead(false);
            notification = notificationRepository.save(notification);

            // Send push notification
            sendPushNotification(userId, notificationType.getTypeName(), dto.getContent());

            // Publish to Kafka
            kafkaTemplate.send("notification-created", notification);

            return Either.right(mapToResponse(notification, notificationType));
        } catch (NotificationException e) {
            logger.error("Notification error: {}", e.getMessage());
            return Either.left(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to create notification: {}", e.getMessage());
            return Either.left("Failed to create notification: " + e.getMessage());
        }
    }

    private void sendPushNotification(Long userId, String title, String body) {
        List<UserDevice> devices = userDeviceRepository.findByUserId(userId);
        for (UserDevice device : devices) {
            try {
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .setToken(device.getDeviceToken())
                        .build();
                firebaseMessaging.send(message);
                logger.info("Push notification sent to user {} on device {}", userId, device.getDeviceToken());
            } catch (Exception e) {
                logger.error("Failed to send push notification to device {}: {}", device.getDeviceToken(), e.getMessage());
            }
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
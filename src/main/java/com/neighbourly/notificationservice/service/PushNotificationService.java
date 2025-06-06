package com.neighbourly.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.neighbourly.notificationservice.entity.UserDevice;
import com.neighbourly.notificationservice.exception.NotificationException;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    public void sendPushNotification(UserDevice device, String title, String body) throws NotificationException {
        if (device == null || device.getDeviceToken() == null || device.getDeviceToken().isEmpty()) {
            throw new NotificationException("Invalid device or device token for push notification");
        }

        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(device.getDeviceToken())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Push notification sent successfully: " + response);
        } catch (FirebaseMessagingException e) {
            throw new NotificationException("Failed to send push notification to device: " + device.getDeviceToken(), e);
        }
    }
}
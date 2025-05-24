package com.neighbourly.notificationservice.config;

import com.neighbourly.commonservice.dispatcher.registry.HandlerRegistry;
import com.neighbourly.notificationservice.command.CreateNotificationCommand;
import com.neighbourly.notificationservice.command.GetUserNotificationsCommand;
import com.neighbourly.notificationservice.command.MarkNotificationAsReadCommand;
import com.neighbourly.notificationservice.handler.CreateNotificationCommandHandler;
import com.neighbourly.notificationservice.handler.GetUserNotificationsCommandHandler;
import com.neighbourly.notificationservice.handler.MarkNotificationAsReadCommandHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationServiceConfig {
    private final HandlerRegistry handlerRegistry;
    private final CreateNotificationCommandHandler createNotificationCommandHandler;
    private  final GetUserNotificationsCommandHandler getUserNotificationsCommandHandler;
    private final MarkNotificationAsReadCommandHandler markNotificationAsReadCommandHandler;

    public NotificationServiceConfig(HandlerRegistry handlerRegistry, CreateNotificationCommandHandler createNotificationCommandHandler, GetUserNotificationsCommandHandler getUserNotificationsCommandHandler, MarkNotificationAsReadCommandHandler markNotificationAsReadCommandHandler) {
        this.handlerRegistry = handlerRegistry;
        this.createNotificationCommandHandler = createNotificationCommandHandler;
        this.getUserNotificationsCommandHandler = getUserNotificationsCommandHandler;
        this.markNotificationAsReadCommandHandler = markNotificationAsReadCommandHandler;
    }


    @PostConstruct
    public void registerHandlers() {
        handlerRegistry.registerHandler(CreateNotificationCommand.class, createNotificationCommandHandler);
        handlerRegistry.registerHandler(GetUserNotificationsCommand.class, getUserNotificationsCommandHandler);
        handlerRegistry.registerHandler(MarkNotificationAsReadCommand.class, markNotificationAsReadCommandHandler);
    }
}

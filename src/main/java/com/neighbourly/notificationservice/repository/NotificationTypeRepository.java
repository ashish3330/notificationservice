package com.neighbourly.notificationservice.repository;

import com.neighbourly.notificationservice.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

  Optional<NotificationType> findByTypeName(String typeName);
}
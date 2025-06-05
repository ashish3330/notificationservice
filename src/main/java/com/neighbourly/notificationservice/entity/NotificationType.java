package com.neighbourly.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "notification_type")
public class NotificationType {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long typeId;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column
    private String description;

}
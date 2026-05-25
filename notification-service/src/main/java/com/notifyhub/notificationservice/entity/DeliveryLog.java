package com.notifyhub.notificationservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="delivery_logs")
@Data
public class DeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "event_type")
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private ChannelType channel;

    @Column(name = "recipient")
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

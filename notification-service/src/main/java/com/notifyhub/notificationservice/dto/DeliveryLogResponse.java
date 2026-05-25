package com.notifyhub.notificationservice.dto;

import lombok.Builder;
import lombok.Data;
import com.notifyhub.notificationservice.entity.ChannelType;
import com.notifyhub.notificationservice.entity.DeliveryStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryLogResponse {
    private Long id;
    private Long eventId;
    private String tenantId;
    private String eventType;
    private ChannelType channel;
    private String recipient;
    private DeliveryStatus status;
    private String errorMessage;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}

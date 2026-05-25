package com.notifyhub.notificationservice.dto;

import com.notifyhub.notificationservice.entity.ChannelType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class KafkaEventMessage {
    private Long eventId;
    private String tenantId;
    private String eventType;
    private ChannelType channel;
    private String recipient;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
}

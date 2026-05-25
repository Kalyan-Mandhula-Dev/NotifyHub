package com.notifyhub.eventservice.dto;

import com.notifyhub.eventservice.entity.ChannelType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class KafkaEventMessage {
    private Long eventId;
    private String tenantId;
    private String eventType;
    private ChannelType channel;
    private String recipient;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;
}

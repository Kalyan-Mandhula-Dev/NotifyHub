package com.notifyhub.eventservice.dto.response;

import com.notifyhub.eventservice.entity.ChannelType;
import com.notifyhub.eventservice.entity.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String tenantId;
    private String eventType;
    private ChannelType channel;
    private String recipient;
    private EventStatus status;
    private LocalDateTime createdAt;
}

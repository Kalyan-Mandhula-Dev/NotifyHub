package com.notifyhub.aiservice.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class EnrichmentRequest {
    private String correlationId;
    private String tenantId;
    private Long eventId;
    private String eventType;
    private Map<String, Object> payload;
    private String templateHtml;
}

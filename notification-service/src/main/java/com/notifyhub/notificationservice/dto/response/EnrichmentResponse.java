package com.notifyhub.notificationservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class EnrichmentResponse {
    private String correlationId;
    private String tenantId;
    private String templateHtml;
}
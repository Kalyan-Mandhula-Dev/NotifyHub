package com.notifyhub.aiservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnrichmentResponse {
    private String correlationId;
    private String tenantId;
    private String templateHtml;
}

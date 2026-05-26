package com.notifyhub.templateservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplateContentResponse {
    private String tenantId;
    private String eventType;
    private String content;
}

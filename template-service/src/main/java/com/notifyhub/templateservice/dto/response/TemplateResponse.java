package com.notifyhub.templateservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TemplateResponse {
    private Long id;
    private String tenantId;
    private String eventType;
    private String templateName;
    private String s3Key;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

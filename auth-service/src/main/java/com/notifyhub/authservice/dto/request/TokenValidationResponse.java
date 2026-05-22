package com.notifyhub.authservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String tenantId;
    private String email;
}

package com.notifyhub.authservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthCredentialResponse {
    private String tenant_id;
    private String token;
    private String email;
    private String message;
}

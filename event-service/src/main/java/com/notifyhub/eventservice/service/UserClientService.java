package com.notifyhub.eventservice.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;

@Component
@Slf4j
public class UserClientService {

    private final WebClient webClient;

    public UserClientService(@Value("${user.service.url}") String userServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(userServiceUrl).build();
    }

    public boolean tenantExists(String tenantId) {
        try {
            webClient.get()
                    .uri("/api/users/tenants/{tenant_id}", tenantId)
                    .retrieve()
                    .bodyToMono(TenantResponse.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            log.error("Error calling user-service: {}", e.getMessage());
            throw new RuntimeException(
                    "User service unavailable: " + e.getMessage()
            );
        }
    }

    @Data
    public static class TenantResponse {
        private String id;
        private String companyName;
        private String email;
    }
}

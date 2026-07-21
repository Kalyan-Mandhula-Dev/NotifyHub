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

    public UserClientService(WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder
                .baseUrl("http://user-service")
                .build();
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

    public boolean hasActiveSubscription(String tenantId, String channel) {
        try {
            SubscriptionCheckResponse response = webClient.get()
                    .uri("/api/users/subscriptions/check?tenantId={tenantId}&channel={channel}",
                            tenantId, channel)
                    .retrieve()
                    .bodyToMono(SubscriptionCheckResponse.class)
                    .block();

            return response != null && response.isSubscribed();

        } catch (Exception e) {
            log.error("Error checking subscription: {}", e.getMessage());
            return false;
        }
    }

    @Data
    private static class SubscriptionCheckResponse {
        private boolean subscribed;
    }

    @Data
    public static class TenantResponse {
        private String id;
        private String companyName;
        private String email;
    }
}

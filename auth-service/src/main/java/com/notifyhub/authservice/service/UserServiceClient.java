package com.notifyhub.authservice.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;

@RestController
public class UserServiceClient {

    private WebClient webClient;

    public UserServiceClient(@Value("${user.service.url}") String userServiceUrl) {
        this.webClient = WebClient.builder().baseUrl(userServiceUrl).build();
    }

    public String createTeant(String companyName, String email) {
        CreateTenantRequest tenantRequest = new CreateTenantRequest();
        tenantRequest.setEmail(email);
        tenantRequest.setCompanyName(companyName);

        try {
            TenantResponse tenant = webClient.post()
                    .uri("/api/users/tenants")
                    .bodyValue(tenantRequest)
                    .retrieve()
                    .bodyToMono(TenantResponse.class)
                    .block();
            return tenant != null ? tenant.getId() : null;

        } catch (WebClientResponseException e) {
            System.out.println("Failed to create tenant in user-service: " + e.getMessage());
            throw new RuntimeException(
                    "Failed to create tenant profile: " + e.getMessage()
            );
        }
    }

    @Data
    public static class TenantResponse {
        private String id;
        private String companyName;
        private String email;
    }

    @Data
    public static class CreateTenantRequest {
        private String companyName;
        private String email;
    }
}

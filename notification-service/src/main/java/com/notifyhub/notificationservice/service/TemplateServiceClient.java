package com.notifyhub.notificationservice.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Slf4j
public class TemplateServiceClient {

    private final WebClient webClient;

    public TemplateServiceClient(
            @Value("${template.service.url}") String templateServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(templateServiceUrl)
                .build();
    }

    public String fetchTemplate(String tenantId, String eventType) {
        try {
            TemplateResponse response = webClient.get()
                    .uri("/api/templates/resolve?tenantId={tenantId}&eventType={eventType}",
                            tenantId, eventType)
                    .retrieve()
                    .bodyToMono(TemplateResponse.class)
                    .block();

            return response != null ? response.getContent() : null;

        } catch (WebClientResponseException.NotFound e) {
            log.warn("No template found for tenant: {} eventType: {}. " +
                    "Using fallback.", tenantId, eventType);
            return null;

        } catch (Exception e) {
            log.warn("Template service unavailable: {}. Using fallback.",
                    e.getMessage());
            return null;
        }
    }

    @Data
    private static class TemplateResponse {
        private String content;
        private String eventType;
        private String tenantId;
    }
}

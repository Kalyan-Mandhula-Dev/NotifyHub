package com.notifyhub.notificationservice.service;

import com.notifyhub.notificationservice.dto.request.EnrichmentRequest;
import com.notifyhub.notificationservice.dto.response.EnrichmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class AIServiceClient {

    private final WebClient webClient;

    @Autowired
    public AIServiceClient(WebClient.Builder loadBalancedWebClientBuilder) {
        this.webClient = loadBalancedWebClientBuilder
                .baseUrl("http://localhost:8086")
                .build();
    }


    public String enrichTemplate(EnrichmentRequest enrichmentRequest) {
        return webClient.post()
                .uri("/api/ai")
                .bodyValue(enrichmentRequest)
                .retrieve()
                .bodyToMono(EnrichmentResponse.class)
                .map(EnrichmentResponse::getTemplateHtml)
                .block();
    }
}

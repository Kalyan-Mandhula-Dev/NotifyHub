package com.notifyhub.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class WebhookService {

    private final WebClient webClient;

    public WebhookService() {
        this.webClient = WebClient.builder().build();
    }

    public void sendWebhook(String webhookUrl, Map<String, Object> payload)
            throws Exception {

        String response = webClient.post()
                .uri(webhookUrl)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Webhook delivered to: {} response: {}", webhookUrl, response);
    }
}

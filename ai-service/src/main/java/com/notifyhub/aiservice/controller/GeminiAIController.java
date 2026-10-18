package com.notifyhub.aiservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notifyhub.aiservice.dto.request.EnrichmentRequest;
import com.notifyhub.aiservice.dto.response.EnrichmentResponse;
import com.notifyhub.aiservice.service.GeminiServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class GeminiAIController {

    private final GeminiServiceClient geminiServiceClient;

    public GeminiAIController(GeminiServiceClient geminiServiceClient) {
        this.geminiServiceClient = geminiServiceClient;
    }

    @PostMapping
    public ResponseEntity<EnrichmentResponse> generate(@RequestBody EnrichmentRequest enrichmentRequest) throws Exception {
        EnrichmentResponse enrichmentResponse = geminiServiceClient.enrichTemplate(enrichmentRequest);
        return new ResponseEntity<>(enrichmentResponse, HttpStatus.OK);
    }
}

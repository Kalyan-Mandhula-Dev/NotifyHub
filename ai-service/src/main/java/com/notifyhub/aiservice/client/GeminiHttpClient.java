package com.notifyhub.aiservice.client;

import com.notifyhub.aiservice.dto.request.GeminiRequest;
import com.notifyhub.aiservice.dto.response.GeminiResponse;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class GeminiHttpClient implements AiClient {

    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;
    private final String model;

    public GeminiHttpClient(@Value("${gemini.base-url}") String baseUrl,
                            @Value("${gemini.api-key}") String apiKey,
                            @Value("${gemini.model}") String model) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    @Retry(name = "geminiClient")
    public AiClient.AiGenerationResult generate(String systemPrompt, String userPrompt) {
        GeminiRequest request = new GeminiRequest(
                new GeminiRequest.SystemInstruction(List.of(new GeminiRequest.Part(systemPrompt))),
                List.of(new GeminiRequest.Content("user", List.of(new GeminiRequest.Part(userPrompt)))),
                new GeminiRequest.GenerationConfig(0.3, 4024));
        try {

            GeminiResponse response = webClient.post()
                    .uri("/v1beta/models/{model}:generateContent", model)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(CALL_TIMEOUT)
                    .block();

            if (response == null || response.usageMetadata() == null || response.getFirstText() == null) {
                throw new IllegalStateException("Invalid response received from Gemini");
            }

            int thoughtsTokens = response.usageMetadata().thoughtsTokenCount();

            // Validate the response (check if it is partial or completed)
            if (!response.candidates().get(0).finishReason().equals("STOP")) {
                log.error("Gemini finish reason: {}", response.candidates().get(0).finishReason());
                log.info("Received response from Gemini: {}", response.getFirstText());
                log.info("thoughtsTokens: {}", thoughtsTokens);
                throw new IllegalStateException("Partial response received from Gemini");
            }


            return new AiClient.AiGenerationResult(
                    response.getFirstText(),
                    response.usageMetadata().promptTokenCount(),
                    response.usageMetadata().candidatesTokenCount());

        } catch (RuntimeException ex) {
            // Exceptions.unwrap() undoes Reactor's ReactiveException wrapping and
            // hands back the real cause (e.g. TimeoutException) so Resilience4j's
            // retry-exceptions list actually matches against it. If ex isn't a
            // Reactor wrapper, unwrap() just returns ex unchanged.
            Throwable unwrapped = Exceptions.unwrap(ex);
            if (unwrapped instanceof RuntimeException re) {
                throw re;
            }
            throw new RuntimeException(unwrapped); // preserves cause for checked exceptions
        }
    }

    @Override
    public String getModelName() {
        return model;
    }
}
package com.notifyhub.aiservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResilienceEventLoggerConfig {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerLoggers() {

        // ---- CIRCUIT BREAKER ----
        circuitBreakerRegistry.circuitBreaker("geminiClient").getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "Circuit breaker 'geminiClient' changed state: {} -> {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> log.warn(
                        "Circuit breaker 'geminiClient' is OPEN — call rejected, going straight to fallback"))
                .onFailureRateExceeded(event -> log.warn(
                        "Circuit breaker 'geminiClient' failure rate exceeded: {}%",
                        event.getFailureRate()));

        // ---- RETRY ----
        retryRegistry.retry("geminiClient").getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retrying 'geminiClient' call — attempt {}, last error: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().getMessage()))
                .onError(event -> log.error(
                        "'geminiClient' call failed after {} attempts, giving up",
                        event.getNumberOfRetryAttempts()));
    }
}
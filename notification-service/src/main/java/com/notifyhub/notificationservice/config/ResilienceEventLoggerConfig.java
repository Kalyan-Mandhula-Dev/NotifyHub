package com.notifyhub.notificationservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
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
    private final TimeLimiterRegistry timeLimiterRegistry;

    @PostConstruct
    public void registerLoggers() {

        // ---- CIRCUIT BREAKER ----
        circuitBreakerRegistry.circuitBreaker("templateService").getEventPublisher()
                .onStateTransition(event -> log.warn(
                        "Circuit breaker 'templateService' changed state: {} -> {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onCallNotPermitted(event -> log.warn(
                        "Circuit breaker 'templateService' is OPEN — call rejected, going straight to fallback"))
                .onFailureRateExceeded(event -> log.warn(
                        "Circuit breaker 'templateService' failure rate exceeded: {}%",
                        event.getFailureRate()));

        // ---- RETRY ----
        retryRegistry.retry("templateService").getEventPublisher()
                .onRetry(event -> log.warn(
                        "Retrying 'templateService' call — attempt {}, last error: {}",
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable().getMessage()))
                .onError(event -> log.error(
                        "'templateService' call failed after {} attempts, giving up",
                        event.getNumberOfRetryAttempts()));

        // ---- TIME LIMITER ----
        timeLimiterRegistry.timeLimiter("templateService").getEventPublisher()
                .onTimeout(event -> log.warn(
                        "'templateService' call timed out — did not respond within configured time limit"))
                .onError(event -> log.error(
                        "'templateService' call errored inside TimeLimiter"));
    }
}

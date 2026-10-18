package com.notifyhub.aiservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(WebClientResponseException.NotFound.class)
    public ResponseEntity<Map<String, Object>> handleModelNotFound(WebClientResponseException.NotFound ex) {
        // Gemini model doesn't exist or isn't available to this key. Permanent — never retry.
        return buildError(HttpStatus.BAD_GATEWAY, "AI model unavailable: " + ex.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.TooManyRequests.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(WebClientResponseException.TooManyRequests ex) {
        // 429 — you've exceeded your quota. Retryable, but needs backoff.
        return buildError(HttpStatus.TOO_MANY_REQUESTS, "AI rate limit hit: " + ex.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.ServiceUnavailable.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(WebClientResponseException.ServiceUnavailable ex) {
        // 503 — Gemini's servers are overloaded. Retryable, the case you just hit.
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "AI service temporarily unavailable: " + ex.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleAiFailure(WebClientResponseException ex) {
        // Catch-all for other 4xx/5xx from Gemini not handled above (e.g. 400 bad request).
        HttpStatus status = ex.getStatusCode().is5xxServerError()
                ? HttpStatus.BAD_GATEWAY
                : HttpStatus.UNPROCESSABLE_ENTITY;
        return buildError(status, "AI call failed (" + ex.getStatusCode() + "): " + ex.getMessage());
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionFailure(WebClientRequestException ex) {
        // Network-level failure — DNS, connection refused, no response at all. Retryable.
        return buildError(HttpStatus.SERVICE_UNAVAILABLE, "Could not reach AI service: " + ex.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, Object>> handleTimeout(TimeoutException ex) {
        // The call took too long and was cut off (relevant once you add Step 5's Time Limiter). Retryable.
        return buildError(HttpStatus.GATEWAY_TIMEOUT, "AI call timed out: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidResponse(IllegalStateException ex) {
        // Your own check — Gemini returned 200 but with no usable content (empty/null response).
        return buildError(HttpStatus.BAD_GATEWAY, "AI returned an invalid response: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("timestamp", LocalDateTime.now().toString());
        return new ResponseEntity<>(map, status);
    }
}

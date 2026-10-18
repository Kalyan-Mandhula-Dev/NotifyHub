// AiStatsResponse.java
package com.notifyhub.aiservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiStatsResponse {
    private String tenantId; // null for global
    private long totalCalls;
    private long totalCompleted;
    private long totalFailed;
    private double failureRate; // percent, 0-100
    private long totalPromptTokens;
    private long totalCompletionTokens;
    private long totalTokens;
    private double avgLatencyMs;
}
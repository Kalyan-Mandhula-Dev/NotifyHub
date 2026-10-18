// AiStatsService.java
package com.notifyhub.aiservice.service;

import com.notifyhub.aiservice.dto.response.AiStatsResponse;
import com.notifyhub.aiservice.entity.UsageLog;
import com.notifyhub.aiservice.repository.UsageLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiStatsService {

    private final UsageLogRepository usageLogRepository;

    public AiStatsResponse getGlobalStats() {
        long completed = usageLogRepository.countByStatus(UsageLog.ResponseStatus.COMPLETED);
        long failed = usageLogRepository.countByStatus(UsageLog.ResponseStatus.FAILED);
        long total = completed + failed;
        long promptTokens = usageLogRepository.sumPromptTokens();
        long completionTokens = usageLogRepository.sumCompletionTokens();

        return AiStatsResponse.builder()
                .tenantId(null)
                .totalCalls(total)
                .totalCompleted(completed)
                .totalFailed(failed)
                .failureRate(total == 0 ? 0.0 : (failed * 100.0) / total)
                .totalPromptTokens(promptTokens)
                .totalCompletionTokens(completionTokens)
                .totalTokens(promptTokens + completionTokens)
                .avgLatencyMs(usageLogRepository.avgLatency())
                .build();
    }

    public AiStatsResponse getTenantStats(String tenantId) {
        long completed = usageLogRepository.countByTenantIdAndStatus(tenantId, UsageLog.ResponseStatus.COMPLETED);
        long failed = usageLogRepository.countByTenantIdAndStatus(tenantId, UsageLog.ResponseStatus.FAILED);
        long total = completed + failed;
        long promptTokens = usageLogRepository.sumPromptTokensByTenant(tenantId);
        long completionTokens = usageLogRepository.sumCompletionTokensByTenant(tenantId);

        return AiStatsResponse.builder()
                .tenantId(tenantId)
                .totalCalls(total)
                .totalCompleted(completed)
                .totalFailed(failed)
                .failureRate(total == 0 ? 0.0 : (failed * 100.0) / total)
                .totalPromptTokens(promptTokens)
                .totalCompletionTokens(completionTokens)
                .totalTokens(promptTokens + completionTokens)
                .avgLatencyMs(usageLogRepository.avgLatencyByTenant(tenantId))
                .build();
    }
}
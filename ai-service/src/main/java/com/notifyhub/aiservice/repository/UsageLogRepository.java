package com.notifyhub.aiservice.repository;

import com.notifyhub.aiservice.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsageLogRepository extends JpaRepository<UsageLog, Long> {

    long countByStatus(UsageLog.ResponseStatus status);

    long countByTenantIdAndStatus(String tenantId, UsageLog.ResponseStatus status);

    @Query("SELECT COALESCE(SUM(u.promptTokens),0) FROM UsageLog u")
    long sumPromptTokens();

    @Query("SELECT COALESCE(SUM(u.promptTokens),0) FROM UsageLog u WHERE u.tenantId = :tenantId")
    long sumPromptTokensByTenant(@Param("tenantId") String tenantId);

    @Query("SELECT COALESCE(SUM(u.completionTokens),0) FROM UsageLog u")
    long sumCompletionTokens();

    @Query("SELECT COALESCE(SUM(u.completionTokens),0) FROM UsageLog u WHERE u.tenantId = :tenantId")
    long sumCompletionTokensByTenant(@Param("tenantId") String tenantId);

    @Query("SELECT COALESCE(AVG(u.latencyMs),0) FROM UsageLog u")
    Double avgLatency();

    @Query("SELECT COALESCE(AVG(u.latencyMs),0) FROM UsageLog u WHERE u.tenantId = :tenantId")
    Double avgLatencyByTenant(@Param("tenantId") String tenantId);

}

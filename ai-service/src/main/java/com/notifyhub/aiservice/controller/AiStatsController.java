// AiStatsController.java
package com.notifyhub.aiservice.controller;

import com.notifyhub.aiservice.dto.response.AiStatsResponse;
import com.notifyhub.aiservice.service.AiStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/stats")
@RequiredArgsConstructor
public class AiStatsController {

    private final AiStatsService aiStatsService;

    @GetMapping
    public AiStatsResponse getGlobalStats() {
        return aiStatsService.getGlobalStats();
    }

    @GetMapping("/{tenantId}")
    public AiStatsResponse getTenantStats(@PathVariable String tenantId) {
        return aiStatsService.getTenantStats(tenantId);
    }
}
package com.notifyhub.notificationservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryStatsResponse {
    private String tenantId;
    private Long totalSent;
    private Long totalFailed;
    private Long totalPending;
}
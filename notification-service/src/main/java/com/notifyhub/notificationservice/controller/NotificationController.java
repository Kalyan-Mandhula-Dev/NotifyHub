package com.notifyhub.notificationservice.controller;

import com.notifyhub.notificationservice.dto.DeliveryLogResponse;
import com.notifyhub.notificationservice.dto.DeliveryStatsResponse;
import com.notifyhub.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/history/{tenantId}")
    public ResponseEntity<List<DeliveryLogResponse>> getHistory(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(
                notificationService.getDeliveryHistory(tenantId)
        );
    }

    @GetMapping("/stats/{tenantId}")
    public ResponseEntity<DeliveryStatsResponse> getStats(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(
                notificationService.getStats(tenantId)
        );
    }
}

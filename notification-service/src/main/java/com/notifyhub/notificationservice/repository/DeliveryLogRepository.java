package com.notifyhub.notificationservice.repository;

import com.notifyhub.notificationservice.entity.DeliveryLog;
import com.notifyhub.notificationservice.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, Long> {

    List<DeliveryLog> findByTenantId(String tenantId);

    List<DeliveryLog> findByTenantIdAndStatus(String tenantId, DeliveryStatus status);

    Long countByTenantIdAndStatus(String tenantId, DeliveryStatus status);
}


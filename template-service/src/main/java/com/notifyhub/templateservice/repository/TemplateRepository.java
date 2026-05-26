package com.notifyhub.templateservice.repository;

import com.notifyhub.templateservice.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    List<Template> findByTenantId(String tenantId);

    Optional<Template> findByTenantIdAndEventTypeAndIsActive(
            String tenantId, String eventType, Boolean isActive
    );

    boolean existsByTenantIdAndEventType(String tenantId, String eventType);
}
package com.notifyhub.eventservice.repository;

import com.notifyhub.eventservice.entity.Event;
import com.notifyhub.eventservice.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTenantId(String tenantId);

    List<Event> findByTenantIdAndStatus(String tenantId, EventStatus status);
}

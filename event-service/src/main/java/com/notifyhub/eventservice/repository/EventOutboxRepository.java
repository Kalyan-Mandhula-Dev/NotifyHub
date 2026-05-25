package com.notifyhub.eventservice.repository;

import com.notifyhub.eventservice.entity.EventOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventOutboxRepository extends JpaRepository<EventOutbox, Long> {

    List<EventOutbox> findByPublished(Boolean published);
}
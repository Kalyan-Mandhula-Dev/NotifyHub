package com.notifyhub.aiservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="usage_log")
@Data
public class UsageLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="tenant_id")
    private String tenantId;

    @Column(name="event_id")
    private Long eventId;

    @Column(name="event_type")
    private String eventType;

    @Column(name="model")
    private String model;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private ResponseStatus status = ResponseStatus.PENDING;

    @Column(name="prompt_tokens")
    private int promptTokens;

    @Column(name="completion_tokens")
    private int completionTokens;

    @Column(name="latency_ms")
    private long latencyMs;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();
    }


    public enum ResponseStatus{
        PENDING, COMPLETED, FAILED
    }



}

package com.notifyhub.eventservice.dto.request;

import com.notifyhub.eventservice.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class TriggerEventRequest {

    private String tenantId;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Channel is required")
    private ChannelType channel;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @NotNull(message = "Payload is required")
    private Map<String, Object> payload;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public ChannelType getChannel() {
        return channel;
    }

    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
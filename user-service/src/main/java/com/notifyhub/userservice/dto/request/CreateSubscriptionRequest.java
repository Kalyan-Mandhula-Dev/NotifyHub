/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.dto.request;

import com.notifyhub.userservice.entity.Subscription.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Dell
 */
public class CreateSubscriptionRequest {

    @NotBlank(message = "Tenant Id cannot be empty")
    private String tenantId;
    
    @NotNull(message = "Channel cannot be null")
    private ChannelType channel;

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId the tenantId to set
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the channel
     */
    public ChannelType getChannel() {
        return channel;
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(ChannelType channel) {
        this.channel = channel;
    }

}

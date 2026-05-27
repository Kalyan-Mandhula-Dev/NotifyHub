/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.dto.response;

import com.notifyhub.userservice.entity.Subscription.ChannelType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Dell
 */
@Data
@Builder
public class SubscriptionResponse {
    private Long id;
    private String tenantId;
    private ChannelType channel;
    private boolean isActive;
    private LocalDateTime createdAt;
}

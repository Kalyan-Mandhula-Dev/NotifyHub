/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.service;

import com.notifyhub.userservice.dto.request.CreateSubscriptionRequest;
import com.notifyhub.userservice.dto.response.SubscriptionResponse;
import com.notifyhub.userservice.entity.Subscription;
import com.notifyhub.userservice.exception.TenantNotFoundException;
import com.notifyhub.userservice.repository.SubscriptionRepository;
import com.notifyhub.userservice.repository.TenantRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Dell
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TenantRepository tenantRepository;

    @Transactional
    public SubscriptionResponse createSubscripton(CreateSubscriptionRequest subscriptionRequest) {
        if (!tenantRepository.existsById(subscriptionRequest.getTenantId())) {
            throw new TenantNotFoundException("Tenant with id " + subscriptionRequest.getTenantId() + " is not found.");
        }

        Subscription subs = new Subscription();
        subs.setTenantId(subscriptionRequest.getTenantId());
        subs.setChannel(subscriptionRequest.getChannel());

        Subscription createdSubs = subscriptionRepository.save(subs);

        return mapToResponse(createdSubs);

    }

    public List<SubscriptionResponse> getSubscriptionsByTenant(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new TenantNotFoundException(
                    "Tenant not found with id: " + tenantId
            );
        }
        return subscriptionRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public boolean hasActiveSubscription(String tenantId, Subscription.ChannelType channel) {
        return subscriptionRepository
                .findByTenantIdAndChannelAndIsActive(tenantId, channel, true)
                .isPresent();
    }

    @Transactional
    public void deleteSubscription(Long subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }

    private SubscriptionResponse mapToResponse(Subscription subs) {
        SubscriptionResponse subsRes = SubscriptionResponse.builder()
                .id(subs.getId())
                .tenantId(subs.getTenantId())
                .channel(subs.getChannel())
                .isActive(subs.isActive())
                .createdAt(subs.getCreatedAt())
                .build();
        return subsRes;
    }
}

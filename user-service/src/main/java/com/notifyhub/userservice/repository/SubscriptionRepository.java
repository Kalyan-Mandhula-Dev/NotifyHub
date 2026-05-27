/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.repository;

import com.notifyhub.userservice.entity.Subscription;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Dell
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByTenantId(String tenantId);

    Optional<Subscription> findByTenantIdAndChannelAndIsActive(
            String tenantId, Subscription.ChannelType channel, Boolean isActive
    );

}

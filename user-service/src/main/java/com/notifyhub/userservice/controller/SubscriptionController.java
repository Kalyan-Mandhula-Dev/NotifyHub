/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.controller;

import com.notifyhub.userservice.dto.request.CreateSubscriptionRequest;
import com.notifyhub.userservice.dto.response.SubscriptionResponse;
import com.notifyhub.userservice.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dell
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest subscriptionRequest) {
        SubscriptionResponse subs = subscriptionService.createSubscripton(subscriptionRequest);
        return new ResponseEntity<>(subs, HttpStatus.OK);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<List<SubscriptionResponse>> getSubscriptions(
            @PathVariable String tenantId) {
        List<SubscriptionResponse> responses
                = subscriptionService.getSubscriptionsByTenant(tenantId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<String> deleteSubscription(
            @PathVariable Long subscriptionId) {
        subscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.ok("Subscription deleted");
    }
}

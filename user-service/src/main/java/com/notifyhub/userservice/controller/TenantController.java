/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.controller;

import com.notifyhub.userservice.dto.request.CreateTenantRequest;
import com.notifyhub.userservice.dto.response.TenantResponse;
import com.notifyhub.userservice.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dell
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/tenants")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<String> testAPI() {
        return new ResponseEntity<>("API is working", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest tenant) {
        TenantResponse createdTenant = tenantService.createTenant(tenant);
        return new ResponseEntity<>(createdTenant, HttpStatus.CREATED);
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> getTenant(@PathVariable String tenantId) {
        TenantResponse response = tenantService.getTenantById(tenantId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<TenantResponse> updateTenant(
            @PathVariable String tenantId,
            @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.updateTenant(tenantId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<String> deactivateTenant(
            @PathVariable String tenantId) {
        tenantService.deactivateTenant(tenantId);
        return ResponseEntity.ok("Tenant deactivated successfully");
    }

}

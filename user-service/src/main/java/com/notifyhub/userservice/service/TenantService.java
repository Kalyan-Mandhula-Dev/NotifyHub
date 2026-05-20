/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.service;

import com.notifyhub.userservice.dto.request.CreateTenantRequest;
import com.notifyhub.userservice.dto.response.TenantResponse;
import com.notifyhub.userservice.entity.Tenant;
import com.notifyhub.userservice.exception.TenantAlreadyExistsException;
import com.notifyhub.userservice.exception.TenantNotFoundException;
import com.notifyhub.userservice.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dell
 */
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantResponse createTenant(CreateTenantRequest requestTenant) {
        if (tenantRepository.existsByEmail(requestTenant.getEmail())) {
            throw new TenantAlreadyExistsException("Tenant with email " + requestTenant.getEmail() + " already exists");
        }

        Tenant tenant = new Tenant();
        tenant.setEmail(requestTenant.getEmail());
        tenant.setCompanyName(requestTenant.getCompanyName());

        Tenant saved = tenantRepository.save(tenant);

        return mapToResponse(saved);
    }
    
    public TenantResponse getTenantById(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new TenantNotFoundException(
                "Tenant not found with id: " + tenantId
            ));
        return mapToResponse(tenant);
    }

    public TenantResponse updateTenant(String tenantId, CreateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(
                "Tenant not found with id: " + tenantId
        ));

        tenant.setCompanyName(request.getCompanyName());
        tenant.setEmail(request.getEmail());

        Tenant updated = tenantRepository.save(tenant);
        return mapToResponse(updated);
    }
    
    public void deactivateTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(
                "Tenant not found with id: " + tenantId
        ));
        tenant.setActive(false);
        tenantRepository.save(tenant);
    }

    public TenantResponse mapToResponse(Tenant tenant) {
        TenantResponse response = TenantResponse.builder()
                .id(tenant.getId())
                .email(tenant.getEmail())
                .companyName(tenant.getCompanyName())
                .isActive(tenant.isActive())
                .createdAt(tenant.getCreatedAt())
                .build();
        return response;
    }

}

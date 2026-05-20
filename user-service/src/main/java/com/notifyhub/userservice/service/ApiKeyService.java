/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.service;

import com.notifyhub.userservice.dto.request.CreateApiKeyRequest;
import com.notifyhub.userservice.dto.response.ApiKeyResponse;
import com.notifyhub.userservice.entity.ApiKey;
import com.notifyhub.userservice.exception.TenantNotFoundException;
import com.notifyhub.userservice.repository.ApiKeyRepository;
import com.notifyhub.userservice.repository.TenantRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dell
 */
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final TenantRepository tenantRepository;

    public ApiKeyResponse generateApiKey(String tenantId) {
        if (!tenantRepository.existsById(tenantId)) {
            throw new TenantNotFoundException("Tenant with id " + tenantId + " is not found.");
        }

        apiKeyRepository.findByTenantIdAndIsActive(tenantId, true)
                .ifPresent((apiKey) -> {
                    apiKey.setIsActive(false);
                    apiKeyRepository.save(apiKey);
                });

        ApiKey apiKey = new ApiKey();
        apiKey.setTenantId(tenantId);
        apiKey.setApiKey(UUID.randomUUID().toString().replace("-", ""));

        ApiKey createdApiKey = apiKeyRepository.save(apiKey);

        return ApiKeyResponse.builder()
                .tenantId(createdApiKey.getTenantId())
                .apiKey(createdApiKey.getApiKey())
                .build();
    }

    public ApiKeyResponse getApiKey(String tenantId) {
        ApiKey apiKey = apiKeyRepository
                .findByTenantIdAndIsActive(tenantId, true)
                .orElseThrow(() -> new TenantNotFoundException(
                "No active API key found for tenant: " + tenantId
        ));
        return ApiKeyResponse.builder()
                .tenantId(apiKey.getTenantId())
                .apiKey(apiKey.getApiKey())
                .createdAt(apiKey.getCreatedAt())
                .build();
    }

}

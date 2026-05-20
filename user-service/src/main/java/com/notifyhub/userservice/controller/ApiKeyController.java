/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.controller;

import com.notifyhub.userservice.dto.response.ApiKeyResponse;
import com.notifyhub.userservice.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dell
 */
@RestController
@RequestMapping("/api/users/apikeys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/{tenantId}")
    public ResponseEntity<ApiKeyResponse> generateKey(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(apiKeyService.generateApiKey(tenantId));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiKeyResponse> getKey(
            @PathVariable String tenantId) {
        return ResponseEntity.ok(apiKeyService.getApiKey(tenantId));
    }

}

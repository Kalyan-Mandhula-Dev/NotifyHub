/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Dell
 */
@Data
@Builder
public class TenantResponse {

    private String id;
    private String companyName;
    private String email;
    private boolean isActive;
    private LocalDateTime createdAt;
}

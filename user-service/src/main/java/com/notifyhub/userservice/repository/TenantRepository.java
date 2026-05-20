/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.notifyhub.userservice.repository;

import com.notifyhub.userservice.entity.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Dell
 */
public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByEmail(String email);
    
    Optional<Tenant> findByEmail(String email);
}

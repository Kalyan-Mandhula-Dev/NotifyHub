package com.notifyhub.authservice.repository;

import com.notifyhub.authservice.entity.AuthCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthCredentials, Long> {

    boolean existsByEmail(String email);

    Optional<AuthCredentials> findByEmail(String email);
}

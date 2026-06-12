package com.notifyhub.authservice.service;

import com.notifyhub.authservice.dto.request.CreateAuthCredentialRequest;
import com.notifyhub.authservice.dto.request.LoginRequest;
import com.notifyhub.authservice.dto.request.TokenValidationResponse;
import com.notifyhub.authservice.dto.response.AuthCredentialResponse;
import com.notifyhub.authservice.entity.AuthCredentials;
import com.notifyhub.authservice.exception.EmailAlreadyExistsException;
import com.notifyhub.authservice.exception.IncorrectEmailOrPassword;
import com.notifyhub.authservice.repository.AuthRepository;
import com.notifyhub.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserServiceClient userServiceClient;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthCredentialResponse registerTenant(CreateAuthCredentialRequest registerRequest) {
        if (authRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Tenant with email " + registerRequest.getEmail() + " already Exists");
        }

        String tenantId = userServiceClient.createTeant(registerRequest.getCompanyName(), registerRequest.getEmail());

        AuthCredentials authCredentials = new AuthCredentials();
        authCredentials.setTenantId(tenantId);
        authCredentials.setCompanyName(registerRequest.getCompanyName());
        authCredentials.setEmail(registerRequest.getEmail());
        authCredentials.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        authRepository.save(authCredentials);

        String token = jwtUtil.generateToken(registerRequest.getEmail(), tenantId);

        return AuthCredentialResponse.builder()
                .tenant_id(tenantId)
                .email(registerRequest.getEmail())
                .token(token)
                .message("Registration Successful !!")
                .build();
    }

    public AuthCredentialResponse login(LoginRequest loginRequest) {

        AuthCredentials authCredentials = authRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IncorrectEmailOrPassword("email or password is incorrect"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), authCredentials.getPassword())) {
            throw new IncorrectEmailOrPassword("email or password is incorrect");
        }

        String token = jwtUtil.generateToken(loginRequest.getEmail(), authCredentials.getTenantId());

        return AuthCredentialResponse.builder()
                .tenant_id(authCredentials.getTenantId())
                .email(loginRequest.getEmail())
                .token(token)
                .message("Login Successful !!")
                .build();
    }

    public TokenValidationResponse validateToken(String token){
        if (!jwtUtil.isTokenValid(token)) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .build();
        }

        return TokenValidationResponse.builder()
                .valid(true)
                .tenantId(jwtUtil.extractTenantId(token))
                .email(jwtUtil.extractEmail(token))
                .build();
    }
}

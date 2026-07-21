package com.notifyhub.authservice.controller;

import com.notifyhub.authservice.dto.request.CreateAuthCredentialRequest;
import com.notifyhub.authservice.dto.request.LoginRequest;
import com.notifyhub.authservice.dto.request.TokenValidationResponse;
import com.notifyhub.authservice.dto.response.AuthCredentialResponse;
import com.notifyhub.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthCredentialResponse> register(@Valid @RequestBody CreateAuthCredentialRequest authCredentialRequest) {
        AuthCredentialResponse registeredTenant = authService.registerTenant(authCredentialRequest);
        return new ResponseEntity<>(registeredTenant, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthCredentialResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthCredentialResponse loginTenant = authService.login(loginRequest);
        return new ResponseEntity<>(loginTenant, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> isTokenValid(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        TokenValidationResponse validationResponse = authService.validateToken(token);
        return new ResponseEntity<>(validationResponse, HttpStatus.OK);
    }
}

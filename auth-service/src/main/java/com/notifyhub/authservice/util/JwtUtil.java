package com.notifyhub.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.expiration.ms}")
    private long expirationMs;

    private SecretKey getSigningKey(String secretKeyString) {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(String email, String tenantId) {
        log.info("Secret Key : {}", secretKeyString);
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId);
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .setIssuedAt(new Date())
                .signWith(getSigningKey(secretKeyString), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractTenantId(String token) {
        return (String) parseClaims(token).get("tenantId");
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKeyString))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

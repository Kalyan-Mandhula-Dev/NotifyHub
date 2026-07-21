package com.notifyhub.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey getSigningKey(String secretKeyString) {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }


    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractTenantId(String token) {
        return (String) parseClaims(token).get("tenantId");
    }

    public boolean isTokenValid(String token) {
        try {
            log.info("Secret Key : {}", secretKeyString);
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

package com.notifyhub.apigateway.filter;

import com.notifyhub.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class JwtAuthFilter {

    public static HandlerFilterFunction<ServerResponse, ServerResponse> apply(JwtUtil jwtUtil) {
        return (request, next) -> {

            try {

                String path = request.path();
                log.info("Gateway received request: {}", path);

                String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);

                if (authHeader == null) {
                    log.warn("No Authorization header on request to: {}", path);
                    return unauthorized("Authorization header is missing");
                }

                if (!authHeader.startsWith("Bearer ")) {
                    return unauthorized("Authorization header must start with Bearer");
                }

                String token = authHeader.substring(7);

                if (!jwtUtil.isTokenValid(token)) {
                    log.warn("Invalid or expired token on request to: {}", path);
                    return unauthorized("Token is invalid or expired");
                }

                String tenantId = jwtUtil.extractTenantId(token);
                String email = jwtUtil.extractEmail(token);

                // ServerRequest is immutable — build a new one with extra headers,
                // then hand it to the next filter/handler in the chain
                ServerRequest modified = ServerRequest.from(request)
                        .header("X-Tenant-Id", tenantId)
                        .header("X-User-Email", email)
                        .build();

                return next.handle(modified);
            } catch (Exception e) {
                log.error("Unexpected error for path {}: {}", request.path(), e.getMessage());
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("status", 500, "message", "Internal gateway error",
                                "timestamp", LocalDateTime.now().toString()));
            }
        };
    }

    private static ServerResponse unauthorized(String message) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", 401,
                        "message", message,
                        "timestamp", LocalDateTime.now().toString()
                ));
    }
}

package com.notifyhub.apigateway.config;

import com.notifyhub.apigateway.filter.JwtAuthFilter;
import com.notifyhub.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;


@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public RouterFunction<ServerResponse> authOpenRoutes() {
        return route("auth-service-open")
                .route(path("/api/auth/register").or(path("/api/auth/login")), http())
                .filter(lb("auth-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authProtectedRoutes() {
        return route("auth-service-protected")
                .route(path("/api/auth/**"), http())
                .filter(JwtAuthFilter.apply(jwtUtil))
                .filter(lb("auth-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes() {
        return route("user-service")
                .route(path("/api/users/**"), http())
                .filter(JwtAuthFilter.apply(jwtUtil))
                .filter(lb("user-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> eventServiceRoutes() {
        return route("event-service")
                .route(path("/api/events/**"), http())
                .filter(JwtAuthFilter.apply(jwtUtil))
                .filter(lb("event-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoutes() {
        return route("notification-service")
                .route(path("/api/notifications/**"), http())
                .filter(JwtAuthFilter.apply(jwtUtil))
                .filter(lb("notification-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> templateServiceRoutes() {
        return route("template-service")
                .route(path("/api/templates/**"), http())
                .filter(JwtAuthFilter.apply(jwtUtil))
                .filter(lb("template-service"))
                .build();
    }

}

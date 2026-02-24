package com.himaloyit.buildnation.gsc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver keyResolver;

    /** Allows Docker Compose (or any env) to override the downstream URI. */
    @Value("${gateway.routes.member-management.uri:http://localhost:8080}")
    private String memberManagementUri;

    public GatewayConfig(RedisRateLimiter redisRateLimiter, KeyResolver keyResolver) {
        this.redisRateLimiter = redisRateLimiter;
        this.keyResolver = keyResolver;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // ── Member Management Route ──────────────────────────────────────
                .route("member-management-route", r -> r
                        .path("/api/v1/members/**")
                        .filters(f -> f
                                // 1. Circuit Breaker — fallback dispatched internally via forward:
                                .circuitBreaker(config -> config
                                        .setName("member-management")
                                        .setFallbackUri("forward:/fallback/member-management"))

                                // 2. Retry — GET only; backs off exponentially 1 s → 5 s (factor 2, jitter)
                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setStatuses(HttpStatus.BAD_GATEWAY, HttpStatus.SERVICE_UNAVAILABLE)
                                        .setBackoff(
                                                Duration.ofSeconds(1),
                                                Duration.ofSeconds(5),
                                                2,
                                                true))

                                // 3. Rate Limiter — Redis-backed; uses injected beans
                                .requestRateLimiter(config -> config
                                        .setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(keyResolver))

                                // 4. Request / response headers
                                .addRequestHeader("X-Gateway-Source", "buildnation-gateway")
                                .addResponseHeader("X-Served-By", "buildnation-gateway"))
                        .uri(memberManagementUri))

                // ── Future route template ────────────────────────────────────────
                // .route("constituency-management-route", r -> r
                //         .path("/api/v1/constituencies/**")
                //         .filters(f -> f
                //                 .circuitBreaker(config -> config
                //                         .setName("constituency-management")
                //                         .setFallbackUri("forward:/fallback/generic"))
                //                 .retry(config -> config.setRetries(3).setMethods(HttpMethod.GET))
                //                 .requestRateLimiter(config -> config
                //                         .setRateLimiter(redisRateLimiter)
                //                         .setKeyResolver(keyResolver))
                //                 .addRequestHeader("X-Gateway-Source", "buildnation-gateway")
                //                 .addResponseHeader("X-Served-By", "buildnation-gateway"))
                //         .uri("${gateway.routes.constituency-management.uri:http://localhost:8082}"))

                .build();
    }
}

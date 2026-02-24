package com.himaloyit.buildnation.gsc.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    /**
     * Rate limiter: 10 requests/sec replenish rate, 20 burst capacity, 1 token per request.
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

    /**
     * Resolves the rate-limit key by X-Forwarded-For header first, then remote address.
     * Marked @Primary to avoid NoUniqueBeanDefinitionException when multiple KeyResolver beans exist.
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                // X-Forwarded-For may contain a comma-separated list; take the first (client) IP
                return Mono.just(forwardedFor.split(",")[0].trim());
            }
            return Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                    .map(addr -> addr.getAddress().getHostAddress())
                    .defaultIfEmpty("unknown");
        };
    }

    // Alternative key resolver — resolves by API key header.
    // Uncomment and register when API-key-based rate limiting is needed.
    //
    // @Bean
    // public KeyResolver apiKeyResolver() {
    //     return exchange -> Mono.justOrEmpty(
    //             exchange.getRequest().getHeaders().getFirst("X-API-Key"))
    //             .defaultIfEmpty("anonymous");
    // }
}

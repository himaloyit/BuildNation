package com.himaloyit.buildnation.gsc.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Reuse existing X-Request-ID or generate a new one
        String requestId = request.getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        final String finalRequestId = requestId;

        // Propagate request-id downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(REQUEST_ID_HEADER, finalRequestId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        MDC.put("requestId", finalRequestId);

        String method = request.getMethod().name();
        String path = request.getURI().getRawPath();
        String remoteAddr = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        log.info("[{}] --> {} {} | RemoteAddr: {}", finalRequestId, method, path, remoteAddr);

        long startTime = System.currentTimeMillis();

        return chain.filter(mutatedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = mutatedExchange.getResponse().getStatusCode() != null
                            ? mutatedExchange.getResponse().getStatusCode().value()
                            : 0;
                    log.info("[{}] <-- {} {} | Status: {} | Duration: {}ms",
                            finalRequestId, method, path, statusCode, duration);
                    MDC.remove("requestId");
                });
    }
}

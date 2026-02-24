package com.himaloyit.buildnation.gsc.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fallback controller invoked by the circuit breaker via internal forward: dispatch.
 * Uses Map<String, Object> deliberately — avoids coupling to member-management's ApiResponse class.
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback/member-management")
    public Mono<ResponseEntity<Map<String, Object>>> memberManagementFallback() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("message", "Member Management service is temporarily unavailable. Please try again later.");
        body.put("errorCode", "CIRCUIT_OPEN");
        body.put("data", null);

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

    @RequestMapping("/fallback/generic")
    public Mono<ResponseEntity<Map<String, Object>>> genericFallback() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("message", "The requested service is temporarily unavailable. Please try again later.");
        body.put("errorCode", "CIRCUIT_OPEN");
        body.put("data", null);

        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}

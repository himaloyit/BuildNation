package com.himaloyit.buildnation.sac.security;

import com.himaloyit.buildnation.sac.domain.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * Author: Rajib Kumer Ghosh
 */

@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;
    private final StringRedisTemplate redisTemplate;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs,
            StringRedisTemplate redisTemplate) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(String email, UserRole role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(accessTokenExpirationMs);
        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(String email) {
        String uuid = UUID.randomUUID().toString();
        long refreshTtlSeconds = 7L * 24 * 60 * 60; // 7 days
        redisTemplate.opsForValue().set("refresh:" + uuid, email, refreshTtlSeconds, TimeUnit.SECONDS);
        return uuid;
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = parseAccessToken(token);
            String jti = claims.getId();
            Boolean blacklisted = redisTemplate.hasKey("blacklist:" + jti);
            return blacklisted == null || !blacklisted;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void blacklistAccessToken(String token) {
        try {
            Claims claims = parseAccessToken(token);
            String jti = claims.getId();
            long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remainingMs > 0) {
                redisTemplate.opsForValue().set("blacklist:" + jti, "true", remainingMs, TimeUnit.MILLISECONDS);
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            // token already invalid — nothing to blacklist
        }
    }

    public String getEmailFromToken(String token) {
        return parseAccessToken(token).getSubject();
    }

    public String getEmailFromRefreshToken(String uuid) {
        return redisTemplate.opsForValue().get("refresh:" + uuid);
    }

    public void deleteRefreshToken(String uuid) {
        redisTemplate.delete("refresh:" + uuid);
    }

    private Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

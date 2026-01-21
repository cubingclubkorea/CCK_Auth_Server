package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    // 블랙리스트 등록
    public void blacklistToken(String token, Instant expiresAt) {
        if (token == null || expiresAt == null) return;
        long secondsRemaining = Duration.between(Instant.now(), expiresAt).getSeconds();
        if (secondsRemaining > 0) {
            redisTemplate.opsForValue().set(
                    BLACKLIST_PREFIX + token,
                    "blacklisted",
                    Duration.ofSeconds(secondsRemaining)
            );
        }
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String token) {
        return token != null && redisTemplate.hasKey(BLACKLIST_PREFIX + token);
    }
}

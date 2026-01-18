package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenCookieManager {
    @Value("${auth.cookie.domain:}") // 설정이 없으면 빈 문자열
    private String cookieDomain;

    @Value("${auth.cookie.secure:false}")
    private Boolean isSecure;

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    public void addTokenCookie(HttpServletResponse response, String name, String value, long maxAge, String path, String sameSite) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(isSecure)
                .path(path)
                .maxAge(maxAge)
                .domain(cookieDomain)
                .sameSite(sameSite)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 쿠키 삭제 (Max-Age 0)
    public void deleteCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(isSecure)
                .path(path)
                .maxAge(0)
                .domain(cookieDomain)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 쿠키에서 값 추출
    public Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

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
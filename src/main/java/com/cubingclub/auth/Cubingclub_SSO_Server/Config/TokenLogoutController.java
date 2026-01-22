package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TokenLogoutController {
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/oauth2/token/revoke")
    public ResponseEntity<Void> revokeToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        
        // Access Token 블랙리스트 추가 (Authorization 헤더)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            Instant expiresAt = Instant.now().plus(Duration.ofDays(7));
            tokenBlacklistService.blacklistToken(accessToken, expiresAt);
        }
        
        // Refresh Token 또는 Access Token 블랙리스트 추가 (token 파라미터)
        if (token != null) {
            // token_type_hint가 refresh_token이면 30일, 아니면 7일
            Duration duration = "refresh_token".equals(tokenTypeHint) 
                ? Duration.ofDays(30) 
                : Duration.ofDays(7);
            Instant expiresAt = Instant.now().plus(duration);

            tokenBlacklistService.blacklistToken(token, expiresAt);
        }
        
        return ResponseEntity.ok().build();
    }
}

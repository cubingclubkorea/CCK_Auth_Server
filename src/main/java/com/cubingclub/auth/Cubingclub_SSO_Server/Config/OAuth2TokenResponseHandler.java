package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2TokenResponseHandler implements AuthenticationSuccessHandler {
    private final TokenBlacklistService tokenBlacklistService;
    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter =
            new OAuth2AccessTokenResponseHttpMessageConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication = 
                (OAuth2AccessTokenAuthenticationToken) authentication;
        
        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();
        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        
        // Refresh Token Rotation 처리
        String grantType = request.getParameter("grant_type");
        if ("refresh_token".equals(grantType)) {
            String oldRefreshToken = request.getParameter("refresh_token");
            if (oldRefreshToken != null && refreshToken != null) {
                // 새로운 refresh token 생성 시 이전 refresh token을 블랙리스트에 추가
                Instant expiresAt = refreshToken.getExpiresAt() != null 
                    ? refreshToken.getExpiresAt() 
                    : Instant.now().plus(7, ChronoUnit.DAYS);

                tokenBlacklistService.blacklistToken(oldRefreshToken, expiresAt);
            }
        }
        
        // Access Token 응답 생성
        OAuth2AccessTokenResponse.Builder builder = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                .tokenType(accessToken.getTokenType())
                .scopes(accessToken.getScopes());
        
        if (accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(Instant.now(), accessToken.getExpiresAt()));
        }
        
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        
        // 응답 전송
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.accessTokenHttpResponseConverter.write(accessTokenResponse, null, httpResponse);
    }
}

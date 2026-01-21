package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Component
public class OAuth2AuthorizationSuccessHandler implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication;
        
        log.info("Authorization code generated, clearing JSESSIONID");
        
        // OAuth2 Authorization Code 응답 URL 생성
        String redirectUri = authorizationCodeRequestAuthentication.getRedirectUri();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(Objects.requireNonNull(redirectUri))
                .queryParam("code", Objects.requireNonNull(authorizationCodeRequestAuthentication.getAuthorizationCode()).getTokenValue());
        
        if (authorizationCodeRequestAuthentication.getState() != null) {
            uriBuilder.queryParam("state", authorizationCodeRequestAuthentication.getState());
        }
        
        // JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        
        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            log.info("Invalidating session after authorization code generation");
            session.invalidate();
        }
        
        // 클라이언트로 리다이렉트
        response.sendRedirect(uriBuilder.build().toUriString());
    }
}

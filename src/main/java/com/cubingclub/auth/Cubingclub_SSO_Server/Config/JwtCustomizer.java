package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    private String returnClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            return authentication.getName();
        }
        return "unknown";
    }
    @Override
    public void customize(JwtEncodingContext context) {
        if(context.getTokenType().getValue().equals("access_token")) {
            context.getClaims().claim("user_host", returnClientId());

            Authentication authentication = context.getPrincipal();
            if (authentication != null && authentication.getAuthorities() != null) {
                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());

                context.getClaims().claim("roles", roles);
            }

            // Exp
            Instant now = Instant.now();
            Instant expTime = now.plus(60, ChronoUnit.MINUTES); // 1Hour
            context.getClaims().expiresAt(expTime);
        }
    }
}
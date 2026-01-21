package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.OAuthClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientCreateDTO {
    private String clientId;
    private String clientName;
    private String clientUri;
    private String clientDescription;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private List<String> scopes;
    private Boolean requireAuthorizationConsent;
    private ClientType clientType;  // 추가 필드

    public OAuthClient toEntity(Account owner) {
        OAuthClient.OAuthClientBuilder builder = OAuthClient.builder()
                .clientId(clientId)
                .clientName(clientName)
                .clientUri(clientUri)
                .clientDescription(clientDescription)
                .clientOwner(owner)
                .redirectUri(redirectUri)
                .postLogoutRedirectUri(postLogoutRedirectUri)
                .requireAuthorizationConsent(requireAuthorizationConsent)
                .clientType(clientType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(false);

        if (scopes != null && !scopes.isEmpty()) {
            builder.scopes(new ArrayList<>(scopes));
        }

        return builder.build();
    }
}

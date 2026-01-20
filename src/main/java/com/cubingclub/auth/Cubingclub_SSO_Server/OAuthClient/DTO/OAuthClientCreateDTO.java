package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.OAuthClient;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.OAuthScopes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
        OAuthClient client = OAuthClient.builder()
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
                .isActive(false)
                .build();

        if (scopes != null) {
            for (String scopeName : scopes) {
                OAuthScopes scope = OAuthScopes.builder()
                        .oauthClient(client)
                        .scope(scopeName)
                        .build();
                client.getScopes().add(scope);
            }
        }

        return client;
    }
}

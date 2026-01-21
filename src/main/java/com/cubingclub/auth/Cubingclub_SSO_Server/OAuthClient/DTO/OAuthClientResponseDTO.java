package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

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
public class OAuthClientResponseDTO {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String clientUri;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private List<String> scopes;
    private ClientType clientType;
    private LocalDateTime clientSecretExpiresAt;

    public OAuthClientResponseDTO(OAuthClient entity){
        this.clientId=entity.getClientId();
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.scopes = entity.getScopes() != null ? new ArrayList<>(entity.getScopes()) : new ArrayList<>();
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
    }
    public OAuthClientResponseDTO(OAuthClient entity, String secret){
        this.clientId=entity.getClientId();
        this.clientSecret=secret;
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.scopes = entity.getScopes() != null ? new ArrayList<>(entity.getScopes()) : new ArrayList<>();
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
    }
}

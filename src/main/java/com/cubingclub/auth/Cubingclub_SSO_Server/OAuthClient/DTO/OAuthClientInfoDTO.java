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
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientInfoDTO {
    private Long id;
    private String clientId;
    private String clientName;
    private String clientUri;
    private String clientDescription;
    private Account clientOwner;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private List<String> scopes;
    private ClientType clientType;  // 추가 필드
    private LocalDateTime clientSecretExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

    public OAuthClientInfoDTO(OAuthClient entity){
        this.id=entity.getId();
        this.clientId=entity.getClientId();
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.clientDescription=entity.getClientDescription();
        this.clientOwner=entity.getClientOwner();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.scopes=entity.getScopes().stream()
                .map(OAuthScopes::getScope) // 문자열로 변환
                .collect(Collectors.toList());
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
        this.createdAt=entity.getCreatedAt();
        this.updatedAt=entity.getUpdatedAt();
        this.isActive=entity.getIsActive();
    }
}

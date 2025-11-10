package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientRequestDTO {
    private String clientId;
    private String clientName;
    private String clientUri;
    private String clientDescription;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private List<String> scopes;
    private boolean requireAuthorizationConsent;
    private ClientType clientType;  // 추가 필드
}

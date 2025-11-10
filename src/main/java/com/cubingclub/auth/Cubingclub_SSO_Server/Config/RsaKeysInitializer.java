package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Repository.OAuthClientRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Repository.RsaKeysRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Service.RsaKeysService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RsaKeysInitializer {

    private final OAuthClientRepository oAuthClientRepository;
    private final OAuthClientService oAuthClientService;
    private final RsaKeysRepository rsaKeysRepository;
    private final RsaKeysService rsaKeysService;

    @Value("${spring.oauth.client.secret}")
    private String clientSecret;
    @Value("${spring.oauth.client.redirect-uri}")
    private String clientRedirect;

    @PostConstruct
    public void initiator() throws Exception {
        // 1. RSA Keys
        String keyId = "bomkey";
        if(rsaKeysRepository.findByIdentifier(keyId).isEmpty()) {
            rsaKeysService.saveRsaKeys(keyId);
            System.out.println("✅ RSA Keys created for: " + keyId);
        }

        // 2. OAuth Client
        String clientId = "cck_ranking";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            OAuthClientRequestDTO rankingRequest=new OAuthClientRequestDTO();
            rankingRequest.setClientId(clientId);
            rankingRequest.setClientName(clientId);
            rankingRequest.setClientDescription("CCK competition's record uploader, viewer");
            rankingRequest.setClientUri("https://ranking.cubingclub.com");
            rankingRequest.setRedirectUri(clientRedirect);
            rankingRequest.setPostLogoutRedirectUri("https://ranking.cubingclub.com");
            rankingRequest.setRequireAuthorizationConsent(true);
            rankingRequest.setClientType(ClientType.NORMAL);
            rankingRequest.setScopes(List.of("openid", "profile", "email")); // 한 번에 scope 설정
            oAuthClientService.saveOAuthClient(rankingRequest, "CCK25ADMI01");
            oAuthClientService.updateSecretDefault(clientId, clientSecret, 365);
            System.out.println("✅ OAuth Client created: " + clientId);
        }
        clientId = "cck_recorder";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            OAuthClientRequestDTO recorderRequest=new OAuthClientRequestDTO();
            recorderRequest.setClientId(clientId);
            recorderRequest.setClientName(clientId);
            recorderRequest.setClientDescription("CCK record upload IoT object");
            recorderRequest.setClientUri("");
            recorderRequest.setPostLogoutRedirectUri("");
            recorderRequest.setRequireAuthorizationConsent(true);
            recorderRequest.setClientType(ClientType.IOT);
            oAuthClientService.saveOAuthClient(recorderRequest, "CCK25ADMI01");
            oAuthClientService.updateSecretDefault(clientId, clientSecret, 365);
            System.out.println("✅ OAuth Client created: " + clientId);
        }
    }
}

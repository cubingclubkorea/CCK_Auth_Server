package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.*;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.OAuthClient;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Repository.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuthClientService {
    private final OAuthClientRepository oAuthClientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;

    public RegisteredClient loadClientByClientId(String clientId){
        return loadClientByResult(oAuthClientRepository.findByClientId(clientId).orElseThrow(()->new CustomizedException(ErrorCode.OAUTH_NOT_FOUND)));
    }

    public RegisteredClient findByIdString(String id){
        return loadClientByResult(oAuthClientRepository.findById(Long.valueOf(id)).orElseThrow(()->new CustomizedException(ErrorCode.OAUTH_NOT_FOUND)));
    }

    public List<OAuthClientInfoDTO> findByClientCCKId(){
        return oAuthClientRepository
                .findByClientOwner_CckId(SecurityContextHolder.getContext().getAuthentication().getName())
                .stream()
                .map(OAuthClientInfoDTO::new)
                .toList();
    }

    public OAuthClientListDTO findForAdmin() {
        List<OAuthClient> clients = oAuthClientRepository.findAll();

        Map<Boolean, List<OAuthClientInfoDTO>> partitioned = clients.stream()
                .map(OAuthClientInfoDTO::new)
                .collect(Collectors.partitioningBy(OAuthClientInfoDTO::getIsActive));

        OAuthClientListDTO result = new OAuthClientListDTO();
        result.setConfirmed(partitioned.get(true));
        result.setWaiting(partitioned.get(false));
        
        return result;
    }

    private String makePassword(){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom r=new SecureRandom();
        StringBuilder pw=new StringBuilder();
        for(int i=0; i<32; i++){
            int index=r.nextInt(chars.length());
            pw.append(chars.charAt(index));
        }
        return pw.toString();
    }

    public OAuthClientResponseDTO updateSecret(OAuthClientSecretDTO oAuthClientSecretDTO) {
        String cckId = SecurityContextHolder.getContext().getAuthentication().getName();
        OAuthClient client = oAuthClientRepository.findByClientId(oAuthClientSecretDTO.getClientId()).orElseThrow();

        if (!client.getIsActive()) {
            throw new CustomizedException(ErrorCode.OAUTH_NOT_ACTIVATED);
        }

        if(!client.getClientOwner().getCckId().equals(cckId)){
            throw new CustomizedException(ErrorCode.OAUTH_OWNER_NOT_MATCH);
        }

        String newSecret = makePassword();
        client.updateSecret(
            bCryptPasswordEncoder.encode(newSecret),
            LocalDateTime.now().plusDays(oAuthClientSecretDTO.getExpireDay())
        );

        return new OAuthClientResponseDTO(client, newSecret);
    }

    public void updateSecretDefault(String clientId, String secret, Integer expireDays){
        OAuthClient client = oAuthClientRepository.findByClientId(clientId).orElseThrow();
        client.updateSecret(
            bCryptPasswordEncoder.encode(secret),
            LocalDateTime.now().plusDays(expireDays)
        );
    }

    public OAuthClientResponseDTO updateConfirm(Long id){
        OAuthClient client = oAuthClientRepository.findById(id).orElseThrow();
        client.toggleActive();
        return new OAuthClientResponseDTO(client);
    }

    public void saveOAuthClient(OAuthClientCreateDTO oAuthClientCreateDTO){
        String cckId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (oAuthClientRepository.findByClientId(oAuthClientCreateDTO.getClientId()).isEmpty()) {
            Account owner = accountRepository.findByCckId(cckId).orElseThrow();
            OAuthClient client = oAuthClientCreateDTO.toEntity(owner);
            oAuthClientRepository.save(client);
        }
    }

    public void deleteOAuthClient(Long id){
        oAuthClientRepository.deleteById(id);
    }

    private RegisteredClient loadClientByResult(OAuthClient result){
        if (!result.getIsActive()) {
            throw new IllegalStateException("Client should be activated: " + result.getClientId());
        }
        if (result.getClientSecretExpiresAt() != null &&
                result.getClientSecretExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Client secret expired for clientId: " + result.getClientId());
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(result.getClientId())
                .clientId(result.getClientId())
                .clientSecret(result.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        if(result.getClientType()== ClientType.IOT){
            builder
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope("limited.read")
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(1))
                            .build());
        }

        else{
            builder
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(result.getRedirectUri())
                    .postLogoutRedirectUri(result.getPostLogoutRedirectUri())
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(result.getRequireAuthorizationConsent()).build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(1))
                            .refreshTokenTimeToLive(Duration.ofDays(30))
                            .reuseRefreshTokens(false) // Refresh Token Rotation 활성화
                            .build());
            
            result.getScopes().forEach(builder::scope);
        }
        return builder.build();
    }
}

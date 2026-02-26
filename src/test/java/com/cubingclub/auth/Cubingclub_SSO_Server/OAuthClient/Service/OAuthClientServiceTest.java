package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.*;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.ClientType;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity.OAuthClient;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Repository.OAuthClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthClientServiceTest {

    @Mock
    private OAuthClientRepository oAuthClientRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private OAuthClientService oAuthClientService;

    @Test
    void loadClientByClientId_성공() {
        // given
        String clientId = "test-client";
        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret("encodedSecret")
                .clientName("Test Client")
                .clientOwner(owner)
                .redirectUri("http://localhost:8080/callback")
                .postLogoutRedirectUri("http://localhost:8080/logout")
                .requireAuthorizationConsent(true)
                .clientType(ClientType.NORMAL)
                .isActive(true)
                .scopes(Arrays.asList("profile", "email"))
                .clientSecretExpiresAt(LocalDateTime.now().plusDays(30))
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when
        RegisteredClient result = oAuthClientService.loadClientByClientId(clientId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(clientId);
        verify(oAuthClientRepository).findByClientId(clientId);
    }

    @Test
    void loadClientByClientId_클라이언트_없음_예외() {
        // given
        String clientId = "test-client";
        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> oAuthClientService.loadClientByClientId(clientId))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OAUTH_NOT_FOUND);
    }

    @Test
    void loadClientByClientId_비활성화된_클라이언트_예외() {
        // given
        String clientId = "test-client";
        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret("encodedSecret")
                .clientOwner(owner)
                .clientType(ClientType.NORMAL)
                .isActive(false)
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when & then
        assertThatThrownBy(() -> oAuthClientService.loadClientByClientId(clientId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Client should be activated");
    }

    @Test
    void loadClientByClientId_만료된_시크릿_예외() {
        // given
        String clientId = "test-client";
        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret("encodedSecret")
                .clientOwner(owner)
                .clientType(ClientType.NORMAL)
                .isActive(true)
                .clientSecretExpiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when & then
        assertThatThrownBy(() -> oAuthClientService.loadClientByClientId(clientId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Client secret expired");
    }

    @Test
    void findByIdString_성공() {
        // given
        String id = "test-client-id";
        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(id)
                .clientSecret("encodedSecret")
                .clientOwner(owner)
                .clientType(ClientType.NORMAL)
                .isActive(true)
                .scopes(Arrays.asList("profile"))
                .redirectUri("http://localhost:8080/callback")
                .postLogoutRedirectUri("http://localhost:8080/logout")
                .requireAuthorizationConsent(true)
                .clientSecretExpiresAt(LocalDateTime.now().plusDays(30))
                .build();

        when(oAuthClientRepository.findById(id)).thenReturn(Optional.of(client));

        // when
        RegisteredClient result = oAuthClientService.findByIdString(id);

        // then
        assertThat(result).isNotNull();
        verify(oAuthClientRepository).findById(id);
    }

    @Test
    void findByClientCCKId_성공() {
        // given
        String cckId = "CCK2501";
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        Account owner = mock(Account.class);
        OAuthClient client1 = OAuthClient.builder()
                .clientId("client1")
                .clientName("Client 1")
                .clientOwner(owner)
                .isActive(true)
                .build();
        OAuthClient client2 = OAuthClient.builder()
                .clientId("client2")
                .clientName("Client 2")
                .clientOwner(owner)
                .isActive(false)
                .build();

        when(oAuthClientRepository.findByClientOwner_CckId(cckId))
                .thenReturn(Arrays.asList(client1, client2));

        // when
        List<OAuthClientInfoDTO> result = oAuthClientService.findByClientCCKId();

        // then
        assertThat(result).hasSize(2);
        verify(oAuthClientRepository).findByClientOwner_CckId(cckId);
    }

    @Test
    void findForAdmin_성공() {
        // given
        Account owner = mock(Account.class);
        OAuthClient activeClient = OAuthClient.builder()
                .clientId("active-client")
                .clientName("Active Client")
                .clientOwner(owner)
                .isActive(true)
                .build();
        OAuthClient waitingClient = OAuthClient.builder()
                .clientId("waiting-client")
                .clientName("Waiting Client")
                .clientOwner(owner)
                .isActive(false)
                .build();

        when(oAuthClientRepository.findAll()).thenReturn(Arrays.asList(activeClient, waitingClient));

        // when
        OAuthClientListDTO result = oAuthClientService.findForAdmin();

        // then
        assertThat(result.getConfirmed()).hasSize(1);
        assertThat(result.getWaiting()).hasSize(1);
        verify(oAuthClientRepository).findAll();
    }

    @Test
    void updateSecret_성공() {
        // given
        String cckId = "CCK2501";
        String clientId = "test-client";
        
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        OAuthClientSecretDTO dto = new OAuthClientSecretDTO();
        dto.setClientId(clientId);
        dto.setExpireDay(30);

        Account owner = mock(Account.class);
        when(owner.getCckId()).thenReturn(cckId);

        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret("oldSecret")
                .clientOwner(owner)
                .isActive(true)
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("newEncodedSecret");

        // when
        OAuthClientResponseDTO result = oAuthClientService.updateSecret(dto);

        // then
        assertThat(result).isNotNull();
        verify(oAuthClientRepository).findByClientId(clientId);
        verify(bCryptPasswordEncoder).encode(anyString());
    }

    @Test
    void updateSecret_비활성화된_클라이언트_예외() {
        // given
        String cckId = "CCK2501";
        String clientId = "test-client";

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        OAuthClientSecretDTO dto = new OAuthClientSecretDTO();
        dto.setClientId(clientId);
        dto.setExpireDay(30);

        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientOwner(owner)
                .isActive(false)
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when & then
        assertThatThrownBy(() -> oAuthClientService.updateSecret(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OAUTH_NOT_ACTIVATED);
    }

    @Test
    void updateSecret_소유자_불일치_예외() {
        // given
        String cckId = "CCK2501";
        String otherCckId = "CCK2502";
        String clientId = "test-client";

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        OAuthClientSecretDTO dto = new OAuthClientSecretDTO();
        dto.setClientId(clientId);
        dto.setExpireDay(30);

        Account owner = mock(Account.class);
        when(owner.getCckId()).thenReturn(otherCckId);

        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientOwner(owner)
                .isActive(true)
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when & then
        assertThatThrownBy(() -> oAuthClientService.updateSecret(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OAUTH_OWNER_NOT_MATCH);
    }

    @Test
    void updateSecretDefault_성공() {
        // given
        String clientId = "test-client";
        String secret = "newSecret";
        Integer expireDays = 30;

        OAuthClient client = mock(OAuthClient.class);
        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));
        when(bCryptPasswordEncoder.encode(secret)).thenReturn("encodedSecret");

        // when
        oAuthClientService.updateSecretDefault(clientId, secret, expireDays);

        // then
        verify(oAuthClientRepository).findByClientId(clientId);
        verify(bCryptPasswordEncoder).encode(secret);
        verify(client).updateSecret(eq("encodedSecret"), any(LocalDateTime.class));
    }

    @Test
    void updateConfirm_성공() {
        // given
        String id = "test-client-id";
        OAuthClient client = mock(OAuthClient.class);
        when(oAuthClientRepository.findById(id)).thenReturn(Optional.of(client));

        // when
        OAuthClientResponseDTO result = oAuthClientService.updateConfirm(id);

        // then
        assertThat(result).isNotNull();
        verify(client).toggleActive();
        verify(oAuthClientRepository).findById(id);
    }

    @Test
    void saveOAuthClient_성공() {
        // given
        String cckId = "CCK2501";

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        OAuthClientCreateDTO dto = mock(OAuthClientCreateDTO.class);
        when(dto.getClientId()).thenReturn("new-client");

        Account owner = mock(Account.class);
        OAuthClient client = mock(OAuthClient.class);

        when(oAuthClientRepository.findByClientId("new-client")).thenReturn(Optional.empty());
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(owner));
        when(dto.toEntity(owner)).thenReturn(client);

        // when
        oAuthClientService.saveOAuthClient(dto);

        // then
        verify(oAuthClientRepository).save(client);
    }

    @Test
    void saveOAuthClient_이미_존재하는_클라이언트_저장하지_않음() {
        // given
        String cckId = "CCK2501";

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        OAuthClientCreateDTO dto = mock(OAuthClientCreateDTO.class);
        when(dto.getClientId()).thenReturn("existing-client");

        OAuthClient existingClient = mock(OAuthClient.class);
        when(oAuthClientRepository.findByClientId("existing-client")).thenReturn(Optional.of(existingClient));

        // when
        oAuthClientService.saveOAuthClient(dto);

        // then
        verify(oAuthClientRepository, never()).save(any());
    }

    @Test
    void deleteOAuthClient_성공() {
        // given
        String id = "test-client-id";

        // when
        oAuthClientService.deleteOAuthClient(id);

        // then
        verify(oAuthClientRepository).deleteById(id);
    }

    @Test
    void loadClientByClientId_IOT_타입_성공() {
        // given
        String clientId = "iot-client";
        Account owner = mock(Account.class);
        OAuthClient client = OAuthClient.builder()
                .clientId(clientId)
                .clientSecret("encodedSecret")
                .clientOwner(owner)
                .clientType(ClientType.IOT)
                .isActive(true)
                .clientSecretExpiresAt(LocalDateTime.now().plusDays(30))
                .build();

        when(oAuthClientRepository.findByClientId(clientId)).thenReturn(Optional.of(client));

        // when
        RegisteredClient result = oAuthClientService.loadClientByClientId(clientId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo(clientId);
        verify(oAuthClientRepository).findByClientId(clientId);
    }
}

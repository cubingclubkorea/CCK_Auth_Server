package com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Entity.RsaKeys;
import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Repository.RsaKeysRepository;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RsaKeysServiceTest {

    @Mock
    private RsaKeysRepository rsaKeysRepository;

    @InjectMocks
    private RsaKeysService rsaKeysService;

    @Test
    void saveRsaKeys_성공() {
        // given
        String identifier = "test-identifier";
        when(rsaKeysRepository.save(any(RsaKeys.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        rsaKeysService.saveRsaKeys(identifier);

        // then
        verify(rsaKeysRepository).save(any(RsaKeys.class));
    }

    @Test
    void loadByJwkSet_성공() throws Exception {
        // given
        String identifier = "test-identifier";
        
        // 실제 RSA 키 생성
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        
        RsaKeys rsaKeys = RsaKeys.builder()
                .identifier(identifier)
                .keyId("test-key-id")
                .publicKey(publicKeyStr)
                .privateKey(privateKeyStr)
                .build();

        when(rsaKeysRepository.findByIdentifier(identifier)).thenReturn(Optional.of(rsaKeys));

        // when
        ImmutableJWKSet<SecurityContext> result = rsaKeysService.loadByJwkSet(identifier);

        // then
        assertThat(result).isNotNull();
        verify(rsaKeysRepository).findByIdentifier(identifier);
    }

    @Test
    void loadByJwkSet_식별자_없음_예외() {
        // given
        String identifier = "non-existent-identifier";
        when(rsaKeysRepository.findByIdentifier(identifier)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rsaKeysService.loadByJwkSet(identifier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Not found RSA Keys");
    }

    @Test
    void loadByJwkSet_잘못된_키_형식_예외() {
        // given
        String identifier = "test-identifier";
        
        RsaKeys rsaKeys = RsaKeys.builder()
                .identifier(identifier)
                .keyId("test-key-id")
                .publicKey("invalid-public-key")
                .privateKey("invalid-private-key")
                .build();

        when(rsaKeysRepository.findByIdentifier(identifier)).thenReturn(Optional.of(rsaKeys));

        // when & then
        assertThatThrownBy(() -> rsaKeysService.loadByJwkSet(identifier))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

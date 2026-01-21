package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthNumberServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthNumberService authNumberService;

    @Test
    void generateAndSave_인증번호_생성_및_저장() {
        // given
        String email = "test@example.com";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        String authNumber = authNumberService.generateAndSave(email);

        // then
        assertThat(authNumber).hasSize(6);
        assertThat(authNumber).matches("\\d{6}");

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(valueOperations).set(
                keyCaptor.capture(),
                valueCaptor.capture(),
                eq(5L),
                eq(TimeUnit.MINUTES)
        );

        assertThat(keyCaptor.getValue()).isEqualTo("auth:" + email);
        assertThat(valueCaptor.getValue()).isEqualTo(authNumber);
    }

    @Test
    void verify_인증번호_일치시_true_반환_및_삭제() {
        // given
        String email = "test@example.com";
        String authNum = "123456";
        String key = "auth:" + email;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(authNum);
        when(redisTemplate.delete(key)).thenReturn(true);

        // when
        Boolean result = authNumberService.verify(email, authNum);

        // then
        assertThat(result).isTrue();
        verify(valueOperations).get(key);
        verify(redisTemplate).delete(key);
    }

    @Test
    void verify_인증번호_불일치시_false_반환() {
        // given
        String email = "test@example.com";
        String authNum = "123456";
        String wrongAuthNum = "654321";
        String key = "auth:" + email;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(authNum);

        // when
        Boolean result = authNumberService.verify(email, wrongAuthNum);

        // then
        assertThat(result).isFalse();
        verify(valueOperations).get(key);
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void verify_저장된_인증번호_없으면_false_반환() {
        // given
        String email = "test@example.com";
        String authNum = "123456";
        String key = "auth:" + email;

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        // when
        Boolean result = authNumberService.verify(email, authNum);

        // then
        assertThat(result).isFalse();
        verify(valueOperations).get(key);
        verify(redisTemplate, never()).delete(anyString());
    }
}

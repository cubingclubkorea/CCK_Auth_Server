package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private AuthNumberService authNumberService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SpringTemplateEngine springTemplateEngine;

    @Mock
    private MailAsyncService mailAsyncService;

    @InjectMocks
    private IssueService issueService;

    @Test
    void sendJoinEmail_회원가입_이메일_발송_성공() {
        // given
        String email = "test@example.com";
        String authNumber = "123456";

        when(accountRepository.existsById(email)).thenReturn(false);
        when(authNumberService.generateAndSave(email)).thenReturn(authNumber);
        when(springTemplateEngine.process(eq("verify"), any(Context.class))).thenReturn("email content");

        // when
        issueService.sendJoinEmail(email);

        // then
        verify(accountRepository).existsById(email);
        verify(authNumberService).generateAndSave(email);
        verify(mailAsyncService).sendEmail(eq(email), anyString(), anyString());
    }

    @Test
    void sendJoinEmail_이미_존재하는_이메일_예외() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsById(email)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> issueService.sendJoinEmail(email))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_EXIST);

        verify(authNumberService, never()).generateAndSave(anyString());
        verify(mailAsyncService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendVerifyPassEmail_비밀번호_찾기_이메일_발송_성공() {
        // given
        String email = "test@example.com";
        String authNumber = "123456";

        when(accountRepository.existsById(email)).thenReturn(true);
        when(authNumberService.generateAndSave(email)).thenReturn(authNumber);
        when(springTemplateEngine.process(eq("verify"), any(Context.class))).thenReturn("email content");

        // when
        issueService.sendVerifyPassEmail(email);

        // then
        verify(accountRepository).existsById(email);
        verify(authNumberService).generateAndSave(email);
        verify(mailAsyncService).sendEmail(eq(email), anyString(), anyString());
    }

    @Test
    void sendVerifyPassEmail_존재하지_않는_이메일_예외() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsById(email)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> issueService.sendVerifyPassEmail(email))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(authNumberService, never()).generateAndSave(anyString());
        verify(mailAsyncService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void issueTemporaryPassword_임시_비밀번호_발급_성공() {
        // given
        PwReissueRequestDTO request = mock(PwReissueRequestDTO.class);
        when(request.getEmail()).thenReturn("test@example.com");
        when(request.getAuthNum()).thenReturn("123456");

        when(authNumberService.verify("test@example.com", "123456")).thenReturn(true);
        when(springTemplateEngine.process(eq("reissue_password"), any(Context.class))).thenReturn("email content");

        // when
        issueService.issueTemporaryPassword(request);

        // then
        verify(authNumberService).verify("test@example.com", "123456");
        verify(mailAsyncService).sendEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void issueTemporaryPassword_인증번호_불일치_예외() {
        // given
        PwReissueRequestDTO request = mock(PwReissueRequestDTO.class);
        when(request.getEmail()).thenReturn("test@example.com");
        when(request.getAuthNum()).thenReturn("123456");

        when(authNumberService.verify("test@example.com", "123456")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> issueService.issueTemporaryPassword(request))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_AUTH_NUMBER_INCORRECT);

        verify(mailAsyncService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}

package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountEditDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountJoinDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private AuthNumberService authNumberService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void checkEmailDuplicate_이메일이_존재하면_true() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsById(email)).thenReturn(true);

        // when
        boolean result = accountService.checkEmailDuplicate(email);

        // then
        assertThat(result).isTrue();
        verify(accountRepository).existsById(email);
    }

    @Test
    void checkEmailDuplicate_이메일이_없으면_false() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsById(email)).thenReturn(false);

        // when
        boolean result = accountService.checkEmailDuplicate(email);

        // then
        assertThat(result).isFalse();
        verify(accountRepository).existsById(email);
    }

    @Test
    void join_회원가입_성공() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(dto.getEnName()).thenReturn("John Doe");
        when(dto.getPassword()).thenReturn("password123");
        when(dto.getPasswordCheck()).thenReturn("password123");
        when(dto.getAuthNum()).thenReturn("123456");

        when(accountRepository.existsById("test@example.com")).thenReturn(false);
        when(authNumberService.verify("test@example.com", "123456")).thenReturn(true);
        when(encoder.encode("password123")).thenReturn("encodedPassword");

        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(2);
        String cckIdFront = "CCK" + yearSuffix + "DOEJ";
        when(accountRepository.findBiggestNum(cckIdFront)).thenReturn(Optional.of(0));

        Account account = mock(Account.class);
        when(dto.toEntity("encodedPassword")).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        // when
        accountService.join(dto);

        // then
        verify(dto).setCckId(anyString());
        verify(accountRepository).save(account);
    }

    @Test
    void join_이메일_중복시_예외() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(accountRepository.existsById("test@example.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> accountService.join(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_EXIST);
    }

    @Test
    void join_비밀번호_길이_부족시_예외() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(dto.getPassword()).thenReturn("short");
        when(accountRepository.existsById("test@example.com")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> accountService.join(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PASSWORD_FORMAT_INCORRECT);
    }

    @Test
    void join_비밀번호_불일치시_예외() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(dto.getPassword()).thenReturn("password123");
        when(dto.getPasswordCheck()).thenReturn("password456");
        when(accountRepository.existsById("test@example.com")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> accountService.join(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PASSWORD_CHECK_INCORRECT);
    }

    @Test
    void join_인증번호_불일치시_예외() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(dto.getPassword()).thenReturn("password123");
        when(dto.getPasswordCheck()).thenReturn("password123");
        when(dto.getAuthNum()).thenReturn("123456");
        when(accountRepository.existsById("test@example.com")).thenReturn(false);
        when(authNumberService.verify("test@example.com", "123456")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> accountService.join(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_AUTH_NUMBER_INCORRECT);
    }

    @Test
    void edit_정보수정_성공() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        when(dto.getPassword()).thenReturn("oldPassword");
        when(dto.getState()).thenReturn("ACTIVE");

        String cckId = "CCK2501";
        Account account = mock(Account.class);
        when(account.getPassword()).thenReturn("encodedPassword");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(encoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // when
        accountService.edit(dto);

        // then
        verify(account).updateState("ACTIVE");
    }

    @Test
    void edit_비밀번호_불일치시_예외() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        when(dto.getPassword()).thenReturn("wrongPassword");

        String cckId = "CCK2501";
        Account account = mock(Account.class);
        when(account.getPassword()).thenReturn("encodedPassword");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(encoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> accountService.edit(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PASSWORD_INCORRECT);
    }

    @Test
    void edit_새비밀번호_불일치시_예외() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        when(dto.getPassword()).thenReturn("oldPassword");
        when(dto.getNewPassword()).thenReturn("newPassword");
        when(dto.getNewPasswordCheck()).thenReturn("differentPassword");

        String cckId = "CCK2501";
        Account account = mock(Account.class);
        when(account.getPassword()).thenReturn("encodedPassword");

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(encoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> accountService.edit(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_PASSWORD_CHECK_INCORRECT);
    }

    @Test
    void getAccountInfo_성공() {
        // given
        String cckId = "CCK2501";
        Account account = mock(Account.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("scope")).thenReturn(java.util.List.of("profile", "email", "phone"));

        // when
        AccountDTO result = accountService.getAccountInfo(jwt);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getAccountInfo_scope_없으면_예외() {
        // given
        String cckId = "CCK2501";
        Account account = mock(Account.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(cckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsStringList("scope")).thenReturn(java.util.List.of());

        // when & then
        // catch 블록에서 모든 예외가 USER_JWT_INVALID로 처리됨
        assertThatThrownBy(() -> accountService.getAccountInfo(jwt))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_JWT_INVALID);
    }

    @Test
    void getNormalInfo_성공() {
        // given
        String cckId = "CCK2501";
        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));

        // when
        PlayerInfo result = accountService.getNormalInfo(cckId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void getNormalInfo_존재하지_않는_계정_예외() {
        // given
        String cckId = "CCK2501";
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountService.getNormalInfo(cckId))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}

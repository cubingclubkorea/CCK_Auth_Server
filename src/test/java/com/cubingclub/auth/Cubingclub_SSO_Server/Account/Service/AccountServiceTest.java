package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountEditDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountJoinDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private AccountService accountService;

    @Test
    void checkEmailDuplicate_이메일이_존재하면_true() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsByEmail(email)).thenReturn(true);

        // when
        boolean result = accountService.checkEmailDuplicate(email);

        // then
        assertThat(result).isTrue();
        verify(accountRepository).existsByEmail(email);
    }

    @Test
    void checkEmailDuplicate_이메일이_없으면_false() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsByEmail(email)).thenReturn(false);

        // when
        boolean result = accountService.checkEmailDuplicate(email);

        // then
        assertThat(result).isFalse();
        verify(accountRepository).existsByEmail(email);
    }

    @Test
    void join_회원가입시_CCK_ID_생성하고_비밀번호_암호화해서_저장한다() {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEnName()).thenReturn("John Doe");
        when(dto.getPassword()).thenReturn("plainPw");

        String encodedPw = "encodedPw";
        when(encoder.encode("plainPw")).thenReturn(encodedPw);

        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(2); // 현재 연도 뒤 2자리
        String cckIdFront = "CCK" + yearSuffix + "DOEJ"; // reorderAndUpperFirst4("John Doe") == "DOEJ"

        when(accountRepository.findBiggestNum(cckIdFront)).thenReturn(Optional.of(0)); // 처음 생성이라고 가정 ⇒ 01

        Account account = mock(Account.class);
        when(dto.toEntity(encodedPw)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);

        final String[] capturedCckId = new String[1];
        doAnswer(invocation -> {
            capturedCckId[0] = invocation.getArgument(0, String.class);
            return null;
        }).when(dto).setCckId(anyString());

        // when
        Account result = accountService.join(dto);

        // then
        assertThat(result).isSameAs(account);
        assertThat(capturedCckId[0]).isEqualTo(cckIdFront + "01"); // CCK + 연도 + DOEJ + 01
        verify(accountRepository).findBiggestNum(cckIdFront);
        verify(encoder).encode("plainPw");
        verify(accountRepository).save(account);
    }

    @Test
    void getUserRole_이메일로_UserRole_조회() {
        // given
        String email = "user@test.com";
        Account account = mock(Account.class);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        // when
        UserRole role = accountService.getUserRole(email);

        // then
        verify(accountRepository).findByEmail(email);
        verify(account).getPosition();
        // 구체적인 enum 값은 프로젝트마다 달라서 여기서는 null 여부만 확인 가능
        assertThat(role).isNull(); // getPosition()이 기본값 null이므로
    }

    @Test
    void getUserRoleWithCCKId_CCK_ID로_UserRole_조회() {
        // given
        String cckId = "CCK250001";
        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));

        // when
        UserRole role = accountService.getUserRoleWithCCKId(cckId);

        // then
        verify(accountRepository).findByCckId(cckId);
        verify(account).getPosition();
        assertThat(role).isNull();
    }

    @Test
    void updateUser_계정이_없으면_minus1() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        String cckId = "CCK250001";
        when(dto.getCckId()).thenReturn(cckId);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.empty());

        // when
        int result = accountService.updateUser(dto);

        // then
        assertThat(result).isEqualTo(-1);
        verify(accountRepository).findByCckId(cckId);
        verify(accountRepository, never()).updateAccountInfo(anyString(), anyString());
        verify(accountRepository, never()).updateAccountPassword(anyString(), anyString());
    }

    @Test
    void updateUser_현재비밀번호_불일치시_minus1() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        String cckId = "CCK250001";
        String inputPassword = "wrongPw";
        String storedEncodedPw = "encodedPw";

        when(dto.getCckId()).thenReturn(cckId);
        when(dto.getPassword()).thenReturn(inputPassword);

        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(account.getPassword()).thenReturn(storedEncodedPw);
        when(encoder.matches(inputPassword, storedEncodedPw)).thenReturn(false);

        // when
        int result = accountService.updateUser(dto);

        // then
        assertThat(result).isEqualTo(-1);
        verify(encoder).matches(inputPassword, storedEncodedPw);
        verify(accountRepository, never()).updateAccountInfo(anyString(), anyString());
        verify(accountRepository, never()).updateAccountPassword(anyString(), anyString());
    }

    @Test
    void updateUser_새비밀번호_없이_상태만_변경하면_updateAccountInfo_호출되고_0리턴() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        String cckId = "CCK250001";
        String inputPassword = "pw";
        String storedEncodedPw = "encodedPw";
        String state = "ACTIVE";

        when(dto.getCckId()).thenReturn(cckId);
        when(dto.getPassword()).thenReturn(inputPassword);
        when(dto.getNewPassword()).thenReturn(null);
        when(dto.getState()).thenReturn(state);

        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(account.getPassword()).thenReturn(storedEncodedPw);
        when(encoder.matches(inputPassword, storedEncodedPw)).thenReturn(true);

        // when
        int result = accountService.updateUser(dto);

        // then
        assertThat(result).isEqualTo(0);
        verify(accountRepository).updateAccountInfo(state, cckId);
        verify(accountRepository, never()).updateAccountPassword(anyString(), anyString());
    }

    @Test
    void updateUser_새비밀번호_일치하면_암호화후_updateAccountPassword_호출되고_0리턴() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        String cckId = "CCK250001";
        String inputPassword = "pw";
        String storedEncodedPw = "encodedPw";
        String newPassword = "newPw";

        when(dto.getCckId()).thenReturn(cckId);
        when(dto.getPassword()).thenReturn(inputPassword);
        when(dto.getNewPassword()).thenReturn(newPassword);
        when(dto.getNewPasswordCheck()).thenReturn(newPassword);

        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(account.getPassword()).thenReturn(storedEncodedPw);
        when(encoder.matches(inputPassword, storedEncodedPw)).thenReturn(true);

        String encodedNewPw = "encodedNewPw";
        when(encoder.encode(newPassword)).thenReturn(encodedNewPw);

        // when
        int result = accountService.updateUser(dto);

        // then
        assertThat(result).isEqualTo(0);
        verify(accountRepository).updateAccountPassword(encodedNewPw, cckId);
    }

    @Test
    void updateUser_새비밀번호와_체크값_다르면_1리턴하고_업데이트_안함() {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        String cckId = "CCK250001";
        String inputPassword = "pw";
        String storedEncodedPw = "encodedPw";

        when(dto.getCckId()).thenReturn(cckId);
        when(dto.getPassword()).thenReturn(inputPassword);
        when(dto.getNewPassword()).thenReturn("newPw");
        when(dto.getNewPasswordCheck()).thenReturn("otherPw");

        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));
        when(account.getPassword()).thenReturn(storedEncodedPw);
        when(encoder.matches(inputPassword, storedEncodedPw)).thenReturn(true);

        // when
        int result = accountService.updateUser(dto);

        // then
        assertThat(result).isEqualTo(1);
        verify(accountRepository, never()).updateAccountPassword(anyString(), anyString());
    }

    @Test
    void getAccountInfoWithCCKId_계정정보_DTO로_반환() {
        // given
        String cckId = "CCK250001";
        Account account = mock(Account.class);
        when(accountRepository.findByCckId(cckId)).thenReturn(Optional.of(account));

        // when
        AccountDTO result = accountService.getAccountInfoWithCCKId(cckId);

        // then
        assertThat(result).isNotNull(); // 생성만 잘 되는지 확인
        verify(accountRepository).findByCckId(cckId);
    }

    @Test
    void updatePassword_이메일없으면_false() {
        // given
        String email = "test@example.com";
        when(accountRepository.existsByEmail(email)).thenReturn(false);

        // when
        boolean result = accountService.updatePassword(email, "newPw");

        // then
        assertThat(result).isFalse();
        verify(accountRepository).existsByEmail(email);
        verify(accountRepository, never()).updateAccountPassword(anyString(), anyString());
    }

    @Test
    void updatePassword_이메일있으면_CCK_ID찾아서_비번암호화후_업데이트하고_true() {
        // given
        String email = "test@example.com";
        String newPassword = "newPw";
        String encodedPw = "encodedNewPw";
        String cckId = "CCK250001";

        when(accountRepository.existsByEmail(email)).thenReturn(true);

        Account account = mock(Account.class);
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));
        when(account.getCckId()).thenReturn(cckId);
        when(encoder.encode(newPassword)).thenReturn(encodedPw);

        // when
        boolean result = accountService.updatePassword(email, newPassword);

        // then
        assertThat(result).isTrue();
        verify(accountRepository).updateAccountPassword(encodedPw, cckId);
    }

    @Test
    void findBySearchTerm_이름검색시_Repository_위임() {
        // given
        String searchTerm = "Lee";
        List<AccountListDTO> expected = new ArrayList<>();
        expected.add(mock(AccountListDTO.class));
        when(accountRepository.findByName(searchTerm)).thenReturn(expected);

        // when
        List<AccountListDTO> result = accountService.findBySearchTerm(searchTerm);

        // then
        assertThat(result).hasSize(1);
        verify(accountRepository).findByName(searchTerm);
    }
}
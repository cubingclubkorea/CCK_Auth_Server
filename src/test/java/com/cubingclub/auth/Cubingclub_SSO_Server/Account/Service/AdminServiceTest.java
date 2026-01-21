package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void findAllList_전체_계정_조회() {
        // given
        Account account1 = mock(Account.class);
        Account account2 = mock(Account.class);
        when(accountRepository.findAll()).thenReturn(List.of(account1, account2));

        // when
        List<AccountDTO> result = adminService.findAllList();

        // then
        assertThat(result).hasSize(2);
        verify(accountRepository).findAll();
    }

    @Test
    void findBySearchTerm_검색어로_계정_조회() {
        // given
        String searchTerm = "John";
        Account account = mock(Account.class);
        when(accountRepository.findBySearchTerm(searchTerm)).thenReturn(List.of(account));

        // when
        List<AccountListDTO> result = adminService.findBySearchTerm(searchTerm);

        // then
        assertThat(result).hasSize(1);
        verify(accountRepository).findBySearchTerm(searchTerm);
    }

    @Test
    void updateRole_권한_업데이트_성공() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.ADMIN);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ROOT);

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.CUSTOMER);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when
        adminService.updateRole(dto);

        // then
        verify(targetAccount).updateRole(UserRole.ADMIN);
    }

    @Test
    void updateRole_CUSTOMER는_권한_변경_불가() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.ADMIN);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.CUSTOMER);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.CUSTOMER);
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    @Test
    void updateRole_ADMIN은_권한_변경_불가() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.CUSTOMER);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ADMIN);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.CUSTOMER);
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    @Test
    void updateRole_ROOT로_변경_불가() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.ROOT);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ROOT);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.CUSTOMER);
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_FORBIDDEN);
    }

    @Test
    void updateRole_ROOT_강등_불가() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.ADMIN);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ROOT);

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.ROOT);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_ROOT_DOWNGRADE);
    }

    @Test
    void updateRole_동일한_권한으로_변경_불가() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");
        when(dto.getPosition()).thenReturn(UserRole.ADMIN);

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ROOT);

        Account targetAccount = mock(Account.class);
        when(targetAccount.getPosition()).thenReturn(UserRole.ADMIN);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.of(targetAccount));

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_IDENTICAL_UPDATE);
    }

    @Test
    void updateRole_존재하지_않는_계정() {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        when(dto.getCckId()).thenReturn("CCK2502");

        String myCckId = "CCK2501";
        Account myAccount = mock(Account.class);
        when(myAccount.getPosition()).thenReturn(UserRole.ROOT);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(myCckId);
        SecurityContextHolder.setContext(securityContext);

        when(accountRepository.findByCckId(myCckId)).thenReturn(Optional.of(myAccount));
        when(accountRepository.findByCckId("CCK2502")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminService.updateRole(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}

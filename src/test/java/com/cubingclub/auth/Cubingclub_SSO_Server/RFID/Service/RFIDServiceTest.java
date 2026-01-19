package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Entity.RFID;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Repository.RFIDRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RFIDServiceTest {

    @Mock
    private RFIDRepository rfidRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RFIDService rfidService;

    @Test
    void createRFID_RFID_생성_성공() {
        // given
        RFIDCreateDTO dto = mock(RFIDCreateDTO.class);
        when(dto.getUid()).thenReturn("1234567890");
        when(dto.getCckId()).thenReturn("CCK2501");

        Account account = mock(Account.class);
        RFID rfid = mock(RFID.class);

        when(rfidRepository.existsById("1234567890")).thenReturn(false);
        when(accountRepository.findByCckId("CCK2501")).thenReturn(Optional.of(account));
        when(dto.toEntity(account)).thenReturn(rfid);
        when(rfidRepository.save(rfid)).thenReturn(rfid);

        // when
        rfidService.createRFID(dto);

        // then
        verify(rfidRepository).existsById("1234567890");
        verify(accountRepository).findByCckId("CCK2501");
        verify(rfidRepository).save(rfid);
    }

    @Test
    void createRFID_이미_존재하는_UID_예외() {
        // given
        RFIDCreateDTO dto = mock(RFIDCreateDTO.class);
        when(dto.getUid()).thenReturn("1234567890");

        when(rfidRepository.existsById("1234567890")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> rfidService.createRFID(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RFID_ALREADY_EXISTS);

        verify(accountRepository, never()).findByCckId(anyString());
        verify(rfidRepository, never()).save(any());
    }

    @Test
    void createRFID_존재하지_않는_계정_예외() {
        // given
        RFIDCreateDTO dto = mock(RFIDCreateDTO.class);
        when(dto.getUid()).thenReturn("1234567890");
        when(dto.getCckId()).thenReturn("CCK2501");

        when(rfidRepository.existsById("1234567890")).thenReturn(false);
        when(accountRepository.findByCckId("CCK2501")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rfidService.createRFID(dto))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(rfidRepository, never()).save(any());
    }

    @Test
    void getPlayerInfo_UID로_플레이어_정보_조회_성공() {
        // given
        String uid = "1234567890";
        Account account = mock(Account.class);
        RFID rfid = mock(RFID.class);

        when(rfid.getAccount()).thenReturn(account);
        when(rfidRepository.findByUidWithAccount(uid)).thenReturn(Optional.of(rfid));

        // when
        PlayerInfo result = rfidService.getPlayerInfo(uid);

        // then
        assertThat(result).isNotNull();
        verify(rfidRepository).findByUidWithAccount(uid);
    }

    @Test
    void getPlayerInfo_존재하지_않는_UID_예외() {
        // given
        String uid = "1234567890";
        when(rfidRepository.findByUidWithAccount(uid)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> rfidService.getPlayerInfo(uid))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RFID_NOT_FOUND);
    }

    @Test
    void deleteRFID_이메일로_RFID_삭제() {
        // given
        String email = "test@example.com";

        // when
        rfidService.deleteRFID(email);

        // then
        verify(rfidRepository).deleteByAccount_email(email);
    }
}

package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountEditDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountJoinDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    @WithMockUser
    void join_회원가입_성공() throws Exception {
        // given
        AccountJoinDTO dto = mock(AccountJoinDTO.class);
        when(dto.getEmail()).thenReturn("test@example.com");
        when(dto.getPassword()).thenReturn("password123");
        when(dto.getEnName()).thenReturn("John Doe");

        doNothing().when(accountService).join(any(AccountJoinDTO.class));

        // when & then
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountService).join(any(AccountJoinDTO.class));
    }

    @Test
    @WithMockUser
    void edit_정보수정_성공() throws Exception {
        // given
        AccountEditDTO dto = mock(AccountEditDTO.class);
        when(dto.getPassword()).thenReturn("oldPassword");

        doNothing().when(accountService).edit(any(AccountEditDTO.class));

        // when & then
        mockMvc.perform(post("/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(accountService).edit(any(AccountEditDTO.class));
    }

    @Test
    @WithMockUser
    void getMyInfo_내_정보_조회_성공() throws Exception {
        // given
        AccountDTO accountDTO = mock(AccountDTO.class);
        when(accountService.getAccountInfo(any())).thenReturn(accountDTO);

        // when & then
        mockMvc.perform(get("/info"))
                .andExpect(status().isOk());

        verify(accountService).getAccountInfo(any());
    }

    @Test
    @WithMockUser
    void getNormalInfo_일반정보_조회_성공() throws Exception {
        // given
        String cckId = "CCK2501";
        PlayerInfo playerInfo = mock(PlayerInfo.class);
        when(accountService.getNormalInfo(cckId)).thenReturn(playerInfo);

        // when & then
        mockMvc.perform(get("/info/{cckId}", cckId))
                .andExpect(status().isOk());

        verify(accountService).getNormalInfo(cckId);
    }
}

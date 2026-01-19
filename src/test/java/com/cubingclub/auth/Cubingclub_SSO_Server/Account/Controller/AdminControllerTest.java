package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AdminService;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service.RFIDService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private RFIDService rfidService;

    @MockitoBean
    private OAuthClientService oAuthClientService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserList_전체_사용자_목록_조회() throws Exception {
        // given
        List<AccountDTO> accounts = List.of(mock(AccountDTO.class));
        when(adminService.findAllList()).thenReturn(accounts);

        // when & then
        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isOk());

        verify(adminService).findAllList();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParticipateList_검색어로_사용자_조회() throws Exception {
        // given
        String searchTerm = "John";
        List<AccountListDTO> accounts = List.of(mock(AccountListDTO.class));
        when(adminService.findBySearchTerm(searchTerm)).thenReturn(accounts);

        // when & then
        mockMvc.perform(get("/admin/search/{searchTerm}", searchTerm))
                .andExpect(status().isOk());

        verify(adminService).findBySearchTerm(searchTerm);
    }

    @Test
    @WithMockUser(roles = "ROOT")
    void updateRole_권한_업데이트() throws Exception {
        // given
        RoleUpdateDTO dto = mock(RoleUpdateDTO.class);
        doNothing().when(adminService).updateRole(any(RoleUpdateDTO.class));

        // when & then
        mockMvc.perform(post("/admin/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(adminService).updateRole(any(RoleUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRFID_RFID_생성() throws Exception {
        // given
        RFIDCreateDTO dto = mock(RFIDCreateDTO.class);
        doNothing().when(rfidService).createRFID(any(RFIDCreateDTO.class));

        // when & then
        mockMvc.perform(post("/admin/rfid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(rfidService).createRFID(any(RFIDCreateDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRFID_RFID_삭제() throws Exception {
        // given
        String email = "test@example.com";
        doNothing().when(rfidService).deleteRFID(email);

        // when & then
        mockMvc.perform(delete("/admin/rfid/{email}", email))
                .andExpect(status().isOk());

        verify(rfidService).deleteRFID(email);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getClientList_OAuth_클라이언트_목록_조회() throws Exception {
        // given
        OAuthClientListDTO dto = mock(OAuthClientListDTO.class);
        when(oAuthClientService.findForAdmin()).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/admin/client"))
                .andExpect(status().isOk());

        verify(oAuthClientService).findForAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void confirmClient_OAuth_클라이언트_승인() throws Exception {
        // given
        Long id = 1L;
        when(oAuthClientService.updateConfirm(id)).thenReturn(null);

        // when & then
        mockMvc.perform(post("/admin/client/confirm/{id}", id))
                .andExpect(status().isOk());

        verify(oAuthClientService).updateConfirm(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void denyClient_OAuth_클라이언트_거절() throws Exception {
        // given
        Long id = 1L;
        doNothing().when(oAuthClientService).deleteOAuthClient(id);

        // when & then
        mockMvc.perform(delete("/admin/client/confirm/{id}", id))
                .andExpect(status().isOk());

        verify(oAuthClientService).deleteOAuthClient(id);
    }
}

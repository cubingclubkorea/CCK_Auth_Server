package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RFIDController.class)
@AutoConfigureMockMvc(addFilters = false)
class RFIDControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RFIDService rfidService;

    @Test
    @WithMockUser
    void getPlayerInfo_RFID로_플레이어_정보_조회() throws Exception {
        // given
        RFIDDTO dto = mock(RFIDDTO.class);
        when(dto.getUid()).thenReturn("1234567890");

        PlayerInfo playerInfo = mock(PlayerInfo.class);
        when(rfidService.getPlayerInfo(anyString())).thenReturn(playerInfo);

        // when & then
        mockMvc.perform(get("/rfid/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(rfidService).getPlayerInfo(anyString());
    }
}

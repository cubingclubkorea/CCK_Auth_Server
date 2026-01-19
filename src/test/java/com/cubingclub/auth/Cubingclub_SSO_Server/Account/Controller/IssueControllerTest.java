package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.EmailRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.IssueService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IssueController.class)
@AutoConfigureMockMvc(addFilters = false)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IssueService issueService;

    @Test
    @WithMockUser
    void signupMailSend_회원가입_이메일_발송() throws Exception {
        // given
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("test@example.com");

        doNothing().when(issueService).sendJoinEmail(anyString());

        // when & then
        mockMvc.perform(post("/issue/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(issueService).sendJoinEmail(anyString());
    }

    @Test
    @WithMockUser
    void verifyPassMailSend_비밀번호_찾기_이메일_발송() throws Exception {
        // given
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("test@example.com");

        doNothing().when(issueService).sendVerifyPassEmail(anyString());

        // when & then
        mockMvc.perform(post("/issue/verify-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(issueService).sendVerifyPassEmail(anyString());
    }

    @Test
    @WithMockUser
    void pwMailSend_임시_비밀번호_발급() throws Exception {
        // given
        PwReissueRequestDTO dto = new PwReissueRequestDTO();
        dto.setEmail("test@example.com");
        dto.setAuthNum("123456");

        doNothing().when(issueService).issueTemporaryPassword(any(PwReissueRequestDTO.class));

        // when & then
        mockMvc.perform(post("/issue/reissue-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(issueService).issueTemporaryPassword(any(PwReissueRequestDTO.class));
    }
}

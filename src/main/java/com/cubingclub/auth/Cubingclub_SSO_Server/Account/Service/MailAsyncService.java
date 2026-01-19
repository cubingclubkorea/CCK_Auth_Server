package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.MailSendDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailAsyncService {
    @Value("${spring.mail.url}")
    private String mailUrl;

    @Value("${spring.mail.api-key}")
    private String mailApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendEmail(String email, String title, String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", mailApiKey);

            MailSendDTO dto = new MailSendDTO(email, title, content);
            HttpEntity<MailSendDTO> request = new HttpEntity<>(dto, headers);

            restTemplate.postForEntity(mailUrl, request, String.class);
        } catch (Exception e) {
            throw new CustomizedException(ErrorCode.MAIL_FAILED, e.getMessage());
        }
    }
}

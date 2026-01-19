package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class IssueService {
    private final AuthNumberService authNumberService;
    private final AccountRepository accountRepository;
    private final SpringTemplateEngine springTemplateEngine;
    private final MailAsyncService mailAsyncService;

    public void sendJoinEmail(String email) {
        if (accountRepository.existsById(email)) {
            throw new CustomizedException(ErrorCode.USER_ALREADY_EXIST);
        }
        sendAuthNumEmail(email);
    }

    public void sendVerifyPassEmail(String email) {
        if (!accountRepository.existsById(email)) {
            throw new CustomizedException(ErrorCode.USER_NOT_FOUND);
        }
        sendAuthNumEmail(email);
    }

    public void issueTemporaryPassword(PwReissueRequestDTO request) {
        if (!authNumberService.verify(request.getEmail(), request.getAuthNum())) {
            throw new CustomizedException(ErrorCode.USER_AUTH_NUMBER_INCORRECT);
        }

        String tempPassword = generateTempPassword();
        String title = "Cubing Club Korea 임시 비밀번호 생성";
        String content = buildPasswordEmailContent(tempPassword);
        mailAsyncService.sendEmail(request.getEmail(), title, content);
    }

    private void sendAuthNumEmail(String email) {
        String authNumber = authNumberService.generateAndSave(email);
        String title = "Cubing Club Korea 인증요청";
        String content = buildEmailAuthContent(authNumber);
        mailAsyncService.sendEmail(email, title, content);
    }

    private String buildEmailAuthContent(String authNumber) {
        Context context = new Context();
        context.setVariable("authNumber", authNumber);
        return springTemplateEngine.process("verify", context);
    }

    private String buildPasswordEmailContent(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        return springTemplateEngine.process("reissue_password", context);
    }

    private String generateTempPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}

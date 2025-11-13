package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.MailSendDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.RandomNum;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.RandomNumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class IssueService {
    private final RandomNumRepository randomNumRepository;
    private final AccountService accountService;
    private final SpringTemplateEngine springTemplateEngine;
    private final MailAsyncService mailAsyncService;

    private String makeAuthNum() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        return randomNumber.toString();
    }

    @Scheduled(fixedDelay = 1000*60*60)
    private void clearAuthNum(){
        List<RandomNum> randList=randomNumRepository.findRandomNumByExpireBefore(LocalDateTime.now());
        for(RandomNum rand:randList) {
            randomNumRepository.deleteByEmail(rand.getEmail());
        }
    }

    private String buildEmailAuthContent(String authNumber) {
        Context context = new Context();
        context.setVariable("authNumber", authNumber);
        return springTemplateEngine.process("verify", context);
    }

    public boolean CheckAuthNum(String email,String authNum){
        if(randomNumRepository.findRandomNumByRandNum(authNum)==null){
            return false;
        }
        else if(!randomNumRepository.findRandomNumByRandNum(authNum).getExpire().isAfter((LocalDateTime.now()))){
            return false;
        }
        else return randomNumRepository.findRandomNumByRandNum(authNum).getEmail().equals(email);
    }

    private Integer sendAuthNumEmail(String email){
        String authNumber=makeAuthNum();
        if(randomNumRepository.findRandomNumByEmail(email)!=null){
            randomNumRepository.deleteByEmail(email);
        }
        while (randomNumRepository.findRandomNumByRandNum(authNumber)!=null){
            authNumber=makeAuthNum();
        }
        randomNumRepository.save(
                RandomNum.builder()
                        .randNum(authNumber)
                        .email(email)
                        .expire(LocalDateTime.now().plusMinutes(5))
                        .build()
        );
        String title = "Cubing Club Korea 인증요청"; // 이메일 제목
        String content = buildEmailAuthContent(authNumber);
        mailAsyncService.sendEmail(email, title, content);
        return 0;
    }

    public Integer sendJoinEmail(String email) {
        if(accountService.checkEmailDuplicate(email)){
            return -2;
        }
        return sendAuthNumEmail(email);
    }

    public Integer sendVerifyPassEmail(String email) {
        if(!accountService.checkEmailDuplicate(email)){
            return -2;
        }
        return sendAuthNumEmail(email);
    }

    private String makeTempPassword(){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom r=new SecureRandom();
        StringBuilder pw=new StringBuilder();
        for(int i=0; i<16; i++){
            int index=r.nextInt(chars.length());
            pw.append(chars.charAt(index));
        }
        return pw.toString();
    }

    private String buildPasswordEmailContent(String tempPassword) {
        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);

        // emailTemplate.html 파일을 템플릿으로 지정 (resources/templates/emailTemplate.html)
        return springTemplateEngine.process("reissue_password", context);
    }

    public Integer sendPasswordEmail(PwReissueRequestDTO pwReissueRequestDTO) {
        if(!CheckAuthNum(pwReissueRequestDTO.getEmail(), pwReissueRequestDTO.getAuthNum())){
            return -2;
        }
        String tempPassword=makeTempPassword();
        if(!accountService.updatePassword(pwReissueRequestDTO.getEmail(), tempPassword)){
            return -1;
        }
        String title = "Cubing Club Korea 임시 비밀번호 생성"; // 이메일 제목
        String content = buildPasswordEmailContent(tempPassword);
        mailAsyncService.sendEmail(pwReissueRequestDTO.getEmail(), title, content);
        return 0;
    }
}

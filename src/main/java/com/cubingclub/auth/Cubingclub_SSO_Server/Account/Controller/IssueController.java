package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.EmailRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/issue")
public class IssueController {
    private final IssueService issueService;
    @PostMapping("/signup")
    public ResponseEntity<?> signupMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        Integer status=issueService.sendJoinEmail(emailDto.getEmail());
        if(status.equals(-2)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
        }
        else if(status.equals(-1)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping ("/verify-password")
    public ResponseEntity<?> verifyPassMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        Integer status=issueService.sendVerifyPassEmail(emailDto.getEmail());
        if(status.equals(-2)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Email doesn't exist");
        }
        else if(status.equals(-1)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping ("/reissue-password")//비밀번호 무작정 바꿀 수도 있기 때문에 이에 대한 대책 필요할 것...
    public ResponseEntity<?> pwMailSend(@Valid @RequestBody PwReissueRequestDTO pwReissueRequestDTO){
        Integer status=issueService.sendPasswordEmail(pwReissueRequestDTO);
        if(Objects.equals(status, -2)){
            return ResponseEntity.badRequest().body("Mail authenticate incorrect");
        }
        else if(Objects.equals(status, -1)){
            return ResponseEntity.internalServerError().body("Failed to create password");
        }
        return ResponseEntity.ok().build();
    }
}

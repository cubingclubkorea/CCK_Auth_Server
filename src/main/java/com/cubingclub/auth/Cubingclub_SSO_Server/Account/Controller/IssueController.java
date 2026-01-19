package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.EmailRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PwReissueRequestDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/issue")
public class IssueController {
    private final IssueService issueService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signupMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        issueService.sendJoinEmail(emailDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Void> verifyPassMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        issueService.sendVerifyPassEmail(emailDto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue-password")//비밀번호 무작정 바꿀 수도 있기 때문에 이에 대한 대책 필요할 것...
    public ResponseEntity<Void> pwMailSend(@Valid @RequestBody PwReissueRequestDTO pwReissueRequestDTO){
        issueService.issueTemporaryPassword(pwReissueRequestDTO);
        return ResponseEntity.ok().build();
    }
}

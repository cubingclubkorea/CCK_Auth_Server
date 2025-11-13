package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountEditDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountJoinDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.InfoType;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AccountService;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.IssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final IssueService issueService;
    @PostMapping("/join")
    public ResponseEntity<?> AddJoin(@Valid @RequestBody AccountJoinDTO accountJoinDTO){
        try {
            if (accountService.checkEmailDuplicate(accountJoinDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
            }
            if (accountJoinDTO.getPassword().length() < 8 || accountJoinDTO.getPassword().length() > 16) {
                return ResponseEntity.badRequest().body("Password size should be in 8~16");
            }
            if (!accountJoinDTO.getPassword().equals(accountJoinDTO.getPasswordCheck())){
                return ResponseEntity.badRequest().body("Password check incorrect");
            }
            if (!issueService.CheckAuthNum(accountJoinDTO.getEmail(), accountJoinDTO.getAuthNum())){
                return ResponseEntity.badRequest().body("AuthNum verify failed");
            }
            Account account=accountService.join(accountJoinDTO);
            return ResponseEntity.ok().body(account);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @PostMapping("/edit")
    public ResponseEntity<?> Edit(@RequestBody AccountEditDTO accountEditDTO){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            accountEditDTO.setCckId(authentication.getName());
            if(accountService.updateUser(accountEditDTO)==0) {
                return ResponseEntity.ok().build();
            }
            else{
                return ResponseEntity.badRequest().build();
            }
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @GetMapping("/info")
    public ResponseEntity<?> GetMyInfo(@AuthenticationPrincipal Jwt jwt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            AccountDTO accountDTO=accountService.getAccountInfoWithCCKId(authentication.getName());
            List<String> scopes = jwt.getClaimAsStringList("scope");
            if(!scopes.contains("profile")){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(!scopes.contains("email")){
                accountDTO.setEmail(null);
            }
            if(!scopes.contains("phone")){
                accountDTO.setPhoneNum(null);
            }
            return ResponseEntity.ok(accountDTO);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @GetMapping("/normal-info")
    public ResponseEntity<?> getNormalInfo(
            @RequestParam InfoType type,
            @RequestParam(required = false) String cckId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object result = accountService.getNormalInfo(type, cckId, authentication.getName());
        return ResponseEntity.ok(result);
    }
}


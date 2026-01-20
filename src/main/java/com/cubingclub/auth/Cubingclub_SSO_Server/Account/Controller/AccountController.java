package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountEditDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountJoinDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<Void> join(@Valid @RequestBody AccountJoinDTO accountJoinDTO){
        accountService.join(accountJoinDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/edit")
    public ResponseEntity<Void> edit(@RequestBody AccountEditDTO accountEditDTO){
        accountService.edit(accountEditDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<AccountDTO> getMyInfo(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(accountService.getAccountInfo(jwt));
    }

    @GetMapping("/info/{cckId}")
    public ResponseEntity<PlayerInfo> getNormalInfo(@PathVariable String cckId) {
        return ResponseEntity.ok(accountService.getNormalInfo(cckId));
    }
}


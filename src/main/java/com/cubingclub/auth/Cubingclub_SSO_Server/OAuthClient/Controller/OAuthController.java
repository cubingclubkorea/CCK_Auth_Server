package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientInfoDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientResponseDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientSecretDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth-client")
public class OAuthController {
    private final OAuthClientService oAuthClientService;

    @GetMapping("/")
    public ResponseEntity<List<OAuthClientInfoDTO>> GetClientList(){
        return ResponseEntity.ok(oAuthClientService.findByClientCCKId());
    }

    @PostMapping("/")
    public ResponseEntity<Void> CreateClient(@RequestBody OAuthClientCreateDTO oAuthClientCreateDTO){
        oAuthClientService.saveOAuthClient(oAuthClientCreateDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/secret")
    public ResponseEntity<OAuthClientResponseDTO> UpdateSecret(@RequestBody OAuthClientSecretDTO oAuthClientSecretDTO){
        return ResponseEntity.ok(oAuthClientService.updateSecret(oAuthClientSecretDTO));
    }
}

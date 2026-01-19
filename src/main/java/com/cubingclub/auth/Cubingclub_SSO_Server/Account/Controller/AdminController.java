package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AdminService;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO.OAuthClientListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service.RFIDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final RFIDService rfidService;
    private final OAuthClientService oAuthClientService;

    @GetMapping("/list")
    public ResponseEntity<List<AccountDTO>> getUserList(){
        return ResponseEntity.ok(adminService.findAllList());
    }

    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<List<AccountListDTO>> getParticipateList(@PathVariable String searchTerm){
        return ResponseEntity.ok(adminService.findBySearchTerm(searchTerm));
    }

    @PostMapping("/role")
    public ResponseEntity<Void> updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO){
        adminService.updateRole(roleUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rfid")
    public ResponseEntity<Void> createRFID(@RequestBody RFIDCreateDTO rfidCreateDTO){
        rfidService.createRFID(rfidCreateDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rfid/{email}")
    public ResponseEntity<Void> deleteRFID(@PathVariable String email){
        rfidService.deleteRFID(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/client")
    public ResponseEntity<OAuthClientListDTO> getClientList(){
        return ResponseEntity.ok(oAuthClientService.findForAdmin());
    }

    @PostMapping("/client/confirm/{id}")
    public ResponseEntity<Void> confirmClient(@PathVariable Long id){
        oAuthClientService.updateConfirm(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/client/confirm/{id}")
    public ResponseEntity<Void> denyClient(@PathVariable Long id){
        oAuthClientService.deleteOAuthClient(id);
        return ResponseEntity.ok().build();
    }
}

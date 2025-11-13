package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AccountService;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service.AdminService;
import com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Service.OAuthClientService;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service.RFIDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AccountService accountService;
    private final AdminService adminService;
    private final RFIDService rfidService;
    private final OAuthClientService oAuthClientService;

    @GetMapping("/list")
    public ResponseEntity<?> UserList(){
        return ResponseEntity.ok(adminService.findAllList());
    }
    @GetMapping("/search/{searchTerm}")
    public ResponseEntity<?> ParticipateList(@PathVariable(value = "searchTerm")String searchTerm){
        return ResponseEntity.ok(accountService.findBySearchTerm(searchTerm));
    }
    @PostMapping("/role")
    public ResponseEntity<?> ChangeRole(@RequestBody RoleUpdateDTO roleUpdateDTO){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserRole myPosition=accountService.getUserRoleWithCCKId(authentication.getName());
        UserRole targetPosition=accountService.getUserRole(roleUpdateDTO.getEmail());
        if (myPosition == UserRole.CUSTOMER || myPosition == UserRole.ADMIN || roleUpdateDTO.getPosition() == UserRole.ROOT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (targetPosition == roleUpdateDTO.getPosition() ||
                (targetPosition == UserRole.ROOT)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.updatePosition(roleUpdateDTO));
    }
    @PostMapping("/rfid")
    public ResponseEntity<?> CreateRFID(@RequestBody RFIDCreateDTO rfidCreateDTO){
        try{
            if(rfidService.createRFID(rfidCreateDTO)==-1){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @DeleteMapping("/rfid/{email}")
    public ResponseEntity<?> DeleteRFID(@PathVariable(name = "email")String email){
        try{
            rfidService.deleteRFID(email);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }

    @GetMapping("/client")
    public ResponseEntity<?> GetClientList(){
        return ResponseEntity.ok(oAuthClientService.findForAdmin(LocalDate.now()));
    }

    @PostMapping("/client/confirm/{id}")
    public ResponseEntity<?> ConfirmClient(@PathVariable(name="id")Long id){
        oAuthClientService.updateConfirm(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/client/confirm/{id}")
    public ResponseEntity<?> DenyClient(@PathVariable(name="id")Long id){
        oAuthClientService.deleteOAuthClient(id);
        return ResponseEntity.ok().build();
    }
}

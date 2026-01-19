package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Controller;

import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service.RFIDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rfid")
public class RFIDController {
    private final RFIDService rfidService;

    @GetMapping("/info")
    public ResponseEntity<?> getPlayerInfo(@RequestBody RFIDDTO rfidDTO){
        return ResponseEntity.ok(rfidService.getPlayerInfo(rfidDTO.getUid()));
    }
}

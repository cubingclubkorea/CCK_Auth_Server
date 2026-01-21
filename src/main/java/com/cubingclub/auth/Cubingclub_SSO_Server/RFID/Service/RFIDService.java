package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.PlayerInfo;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Repository.RFIDRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RFIDService {
    private final RFIDRepository rfidRepository;
    private final AccountRepository accountRepository;

    public void createRFID(RFIDCreateDTO rfidCreateDTO) {
        if(rfidRepository.existsById(rfidCreateDTO.getUid())){
            throw new CustomizedException(ErrorCode.RFID_ALREADY_EXISTS);
        }
        rfidRepository.save(rfidCreateDTO.toEntity(accountRepository.findByCckId(rfidCreateDTO.getCckId()).orElseThrow(()->new CustomizedException(ErrorCode.USER_NOT_FOUND))));
    }

    public PlayerInfo getPlayerInfo(String uid){
        return new PlayerInfo(rfidRepository.findByUidWithAccount(uid).orElseThrow(()->new CustomizedException(ErrorCode.RFID_NOT_FOUND)).getAccount());
    }

    public void deleteRFID(String cckId){
        rfidRepository.deleteByAccount_CckId(cckId);
    }
}

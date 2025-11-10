package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Entity.RFID;
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
    public Integer createRFID(RFIDCreateDTO rfidCreateDTO) {
        if (rfidRepository.findAccountByUid(rfidCreateDTO.getUid()) == null){
            rfidRepository.save(rfidCreateDTO.toEntity(accountRepository));
            return 0;
        }
        else{
            return -1;
        }
    }
    public AccountDTO getAccountInfo(String uid){
        RFID rfid=rfidRepository.findAccountByUid(uid);
        if(rfid==null){
            return null;
        }
        return new AccountDTO(rfid.getAccount());
    }
    public String getEmail(String uid){
        return rfidRepository.findEmailByUid(uid);
    }
    public void deleteRFID(String email){
        rfidRepository.deleteByAccount_email(email);
    }
}

package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AccountRepository accountRepository;
    public List<AccountDTO> findAllList(){
        List<Account> accountList=accountRepository.findAll();
        List<AccountDTO> accountDTOList=new ArrayList<>();
        for(Account a:accountList){
            accountDTOList.add(new AccountDTO(a));
        }
        return accountDTOList;
    }
    public String updatePosition(RoleUpdateDTO roleUpdateDTO){
        return accountRepository.save(accountRepository.findByEmail(roleUpdateDTO.getEmail()).orElseThrow().roleUpdate(roleUpdateDTO.getPosition())).getEmail();
    }
}

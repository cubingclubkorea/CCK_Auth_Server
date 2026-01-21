package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.RoleUpdateDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
    private final AccountRepository accountRepository;

    public List<AccountDTO> findAllList(){
        return accountRepository.findAll()
                .stream()
                .map(AccountDTO::new)
                .toList();
    }

    public List<AccountListDTO> findBySearchTerm(String searchTerm){
        return accountRepository.findBySearchTerm(searchTerm).stream().map(AccountListDTO::new).toList();
    }

    public void updateRole(RoleUpdateDTO roleUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserRole myPosition = getUserRoleWithCCKId(authentication.getName());
        UserRole targetPosition = getUserRoleWithCCKId(roleUpdateDTO.getCckId());
        log.info(myPosition.toString());
        if (myPosition == UserRole.CUSTOMER || myPosition == UserRole.ADMIN || roleUpdateDTO.getPosition() == UserRole.ROOT) {
            throw new CustomizedException(ErrorCode.ADMIN_FORBIDDEN);
        }

        if (targetPosition == roleUpdateDTO.getPosition()) {
            throw new CustomizedException(ErrorCode.ADMIN_IDENTICAL_UPDATE);
        }

        if(targetPosition == UserRole.ROOT){
            throw new CustomizedException(ErrorCode.ADMIN_ROOT_DOWNGRADE);
        }

        Account account=accountRepository.findByCckId(roleUpdateDTO.getCckId()).orElseThrow(()->new CustomizedException(ErrorCode.USER_NOT_FOUND));
        account.updateRole(roleUpdateDTO.getPosition());
    }

    private UserRole getUserRoleWithCCKId(String cckId){
        log.info("Retrieving user role for CCK ID: " + cckId);
        return accountRepository.findByCckId(cckId).orElseThrow(()->new CustomizedException(ErrorCode.USER_NOT_FOUND)).getPosition();
    }
}

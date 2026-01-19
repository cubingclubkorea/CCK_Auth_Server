package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.*;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Entity.ErrorCode;
import com.cubingclub.auth.Cubingclub_SSO_Server.Exception.Exceptions.CustomizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthNumberService authNumberService;  // IssueService 대신 AuthNumberService만 사용

    private String generateCckId(String enName) {
        String cckIdMid = reorderAndUpperFirst4(enName);
        String cckIdFront = "CCK" + getYearSuffix() + cckIdMid;
        int backNum=accountRepository.findBiggestNum(cckIdFront).orElse(0);
        String countStr = String.format("%02d", backNum + 1);

        return cckIdFront + countStr;
    }

    private String reorderAndUpperFirst4(String input) {
        if (input == null || !input.contains(" ")) {
            return input != null && input.length() >= 4
                    ? input.toUpperCase().substring(0, 4)
                    : input != null ? input.toUpperCase() : null;
        }

        int firstSpaceIndex = input.indexOf(" ");
        String firstWord = input.substring(0, firstSpaceIndex);
        String rest = input.substring(firstSpaceIndex + 1);

        String reordered = (rest + firstWord).toUpperCase();
        return reordered.length() >= 4 ? reordered.substring(0, 4) : reordered;
    }

    private String getYearSuffix() {
        int year = java.time.LocalDate.now().getYear();
        return String.valueOf(year).substring(2); // 뒤 2자리
    }

    public boolean checkEmailDuplicate(String email){
        return accountRepository.existsById(email);
    }

    public void join(AccountJoinDTO accountJoinDTO){
        String cckId=generateCckId(accountJoinDTO.getEnName());
        accountJoinDTO.setCckId(cckId);
        if (checkEmailDuplicate(accountJoinDTO.getEmail())) {
            throw new CustomizedException(ErrorCode.USER_ALREADY_EXIST);
        }
        if (accountJoinDTO.getPassword().length() < 8 || accountJoinDTO.getPassword().length() > 16) {
            throw new CustomizedException(ErrorCode.USER_PASSWORD_FORMAT_INCORRECT);
        }
        if (!accountJoinDTO.getPassword().equals(accountJoinDTO.getPasswordCheck())){
            throw new CustomizedException(ErrorCode.USER_PASSWORD_CHECK_INCORRECT);
        }
        if (!authNumberService.verify(accountJoinDTO.getEmail(), accountJoinDTO.getAuthNum())){
            throw new CustomizedException(ErrorCode.USER_AUTH_NUMBER_INCORRECT);
        }
        accountRepository.save(accountJoinDTO.toEntity(encoder.encode(accountJoinDTO.getPassword())));
    }

    public void edit(AccountEditDTO accountEditDTO) {
        String cckId = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByCckId(cckId)
                .orElseThrow(() -> new CustomizedException(ErrorCode.USER_NOT_FOUND));

        if (!encoder.matches(accountEditDTO.getPassword(), account.getPassword())) {
            throw new CustomizedException(ErrorCode.USER_PASSWORD_INCORRECT);
        }

        // 상태 정보 변경 (JPA 변경 감지)
        if (accountEditDTO.getState() != null) {
            account.updateState(accountEditDTO.getState());
        }

        // 비밀번호 변경 (JPA 변경 감지)
        if (accountEditDTO.getNewPassword() != null) {
            if (!accountEditDTO.getNewPassword().equals(accountEditDTO.getNewPasswordCheck())) {
                throw new CustomizedException(ErrorCode.USER_PASSWORD_CHECK_INCORRECT);
            }
            account.updatePassword(encoder.encode(accountEditDTO.getNewPassword()));
        }
    }

    public AccountDTO getAccountInfo(Jwt jwt){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            AccountDTO accountDTO=new AccountDTO(accountRepository.findByCckId(authentication.getName()).orElseThrow(()->new CustomizedException(ErrorCode.USER_NOT_FOUND)));
            List<String> scopes = jwt.getClaimAsStringList("scope");
            if(!scopes.contains("profile")){
                throw new CustomizedException(ErrorCode.USER_SCOPE_FORBIDDEN);
            }
            if(!scopes.contains("email")){
                accountDTO.setEmail(null);
            }
            if(!scopes.contains("phone")){
                accountDTO.setPhoneNum(null);
            }
            return accountDTO;
        }
        catch(Exception e){
            throw new CustomizedException(ErrorCode.USER_JWT_INVALID);
        }
    }

    public PlayerInfo getNormalInfo(String cckId) {
        return new PlayerInfo(accountRepository.findByCckId(cckId).orElseThrow(()->new CustomizedException(ErrorCode.USER_NOT_FOUND)));
    }
}

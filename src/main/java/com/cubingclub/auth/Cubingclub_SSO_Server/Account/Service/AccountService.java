package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.*;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.InfoType;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder;

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
        return accountRepository.existsByEmail(email);
    }
    public Account join(AccountJoinDTO accountJoinDTO){
        String cckId=generateCckId(accountJoinDTO.getEnName());
        accountJoinDTO.setCckId(cckId);
        return accountRepository.save(accountJoinDTO.toEntity(encoder.encode(accountJoinDTO.getPassword())));
    }
    public UserRole getUserRole(String email){
        return accountRepository.findByEmail(email).get().getPosition();
    }
    public UserRole getUserRoleWithCCKId(String cckId){
        return accountRepository.findByCckId(cckId).get().getPosition();
    }
    public int updateUser(AccountEditDTO accountEditDTO){
        Optional<Account> account=accountRepository.findByCckId(accountEditDTO.getCckId());

        if(account.isEmpty()){
            return -1;
        }
        if(!encoder.matches(accountEditDTO.getPassword(), account.get().getPassword())){
            return -1;
        }
        if(accountEditDTO.getNewPassword()==null){
            accountRepository.updateAccountInfo(accountEditDTO.getState(), accountEditDTO.getCckId());
            return 0;
        }
        else{
            if(accountEditDTO.getNewPassword().equals(accountEditDTO.getNewPasswordCheck())) {
                accountRepository.updateAccountPassword(encoder.encode(accountEditDTO.getNewPassword()), accountEditDTO.getCckId());
                return 0;
            }
            else{
                return 1;
            }
        }
    }
    public AccountDTO getAccountInfoWithCCKId(String cckId){
        return new AccountDTO(accountRepository.findByCckId(cckId).get());
    }

    public boolean updatePassword(String email, String password){
        if(accountRepository.existsByEmail(email)){
            String cckId=accountRepository.findByEmail(email).orElseThrow().getCckId();
            accountRepository.updateAccountPassword(encoder.encode(password), cckId);
            return true;
        }
        return false;
    }

    public List<AccountListDTO> findBySearchTerm(String searchTerm){
        return accountRepository.findByName(searchTerm);
    }

    public Object getNormalInfo(InfoType type, String cckId, String email) {

        boolean isSelf = (cckId == null);

        return switch (type) {
            case judge -> isSelf
                    ? findJudgeByEmail(email)
                    : findJudgeByCckId(cckId);
            case player -> isSelf
                    ? findPlayerByEmail(email)
                    : findPlayerByCckId(cckId);
        };

    }
    public JudgeInfo findJudgeByEmail(String email){
        return new JudgeInfo(accountRepository.findByEmail(email).orElseThrow());
    }
    public JudgeInfo findJudgeByCckId(String cckId){
        return new JudgeInfo(accountRepository.findByCckId(cckId).orElseThrow());
    }
    public PlayerInfo findPlayerByEmail(String email){
        return new PlayerInfo(accountRepository.findByEmail(email).orElseThrow());
    }
    public PlayerInfo findPlayerByCckId(String cckId){
        return new PlayerInfo(accountRepository.findByCckId(cckId).orElseThrow());
    }
}

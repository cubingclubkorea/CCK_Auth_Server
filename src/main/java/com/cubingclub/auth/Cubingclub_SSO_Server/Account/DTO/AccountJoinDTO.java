package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Gender;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AccountJoinDTO {
    @NotBlank
    private String email;
    private String name;
    private String enName;
    @NotBlank
    private String password;
    private String passwordCheck;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private String state;
    private Gender gender;
    private String authNum;
    private String cckId;
    private String wcaId;
    private String phoneNum;
    public Account toEntity(String encodedPassword){
        return Account.builder()
                .email(this.email)
                .name(this.name)
                .enName(this.enName)
                .password(encodedPassword)
                .position(UserRole.CUSTOMER)
                .birthDate(this.birthDate)
                .state(this.state)
                .gender(this.gender)
                .cckId(this.cckId)
                .wcaId(this.wcaId)
                .phoneNum(this.phoneNum)
                .build();
    }
}

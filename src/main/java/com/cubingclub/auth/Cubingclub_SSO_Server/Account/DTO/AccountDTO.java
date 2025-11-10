package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Gender;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    @NotBlank
    private String email;
    private String name;
    private String enName;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private UserRole position;
    private String state;
    private Gender gender;
    private String cckId;
    private String wcaId;
    private String phoneNum;
    public AccountDTO(Account entity){
        this.email=entity.getEmail();
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.birthDate=entity.getBirthDate();
        this.state=entity.getState();
        this.gender=entity.getGender();
        this.position=entity.getPosition();
        this.wcaId=entity.getWcaId();
        this.cckId=entity.getCckId();
        this.phoneNum=entity.getPhoneNum();
    }
    public static AccountDTO from(Account entity) {
        if (entity == null) {
            return null;
        }
        return new AccountDTO(entity);
    }
}

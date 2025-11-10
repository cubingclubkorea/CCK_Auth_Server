package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaskedAccountDTO {
    private String name;
    private String enName;
    private String state;
    private Gender gender;
    private String cckId;
    private String wcaId;
    public MaskedAccountDTO(Account entity){
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.state=entity.getState();
        this.gender=entity.getGender();
        this.cckId=entity.getCckId();
        this.wcaId=entity.getWcaId();
    }
}

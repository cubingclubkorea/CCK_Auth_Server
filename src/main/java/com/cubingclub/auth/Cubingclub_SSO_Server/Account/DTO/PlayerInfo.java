package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Gender;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInfo {
    private String name;
    private String enName;
    private String state;
    private String cckId;
    private String wcaId;
    private Gender gender;
    private UserRole position;

    public PlayerInfo(Account entity){
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.state=entity.getState();
        this.cckId=entity.getCckId();
        this.wcaId=entity.getWcaId();
        this.gender=entity.getGender();
        this.position=entity.getPosition();
    }
}

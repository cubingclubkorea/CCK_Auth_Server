package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInfo {
    String name;
    String state;
    String cckId;

    public PlayerInfo(Account entity){
        this.name=entity.getName();
        this.state=entity.getState();
        this.cckId=entity.getCckId();
    }
}

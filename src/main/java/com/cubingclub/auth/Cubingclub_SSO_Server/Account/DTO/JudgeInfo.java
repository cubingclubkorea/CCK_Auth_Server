package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JudgeInfo {
    String name;
    String cckId;

    public JudgeInfo(Account entity){
        this.name=entity.getName();
        this.cckId=entity.getCckId();
    }
}


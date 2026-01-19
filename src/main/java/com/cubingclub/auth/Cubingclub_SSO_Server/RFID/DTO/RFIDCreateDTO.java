package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Entity.RFID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RFIDCreateDTO {
    private String cckId;
    private String uid;

    public RFID toEntity(Account account){
        return RFID
                .builder()
                .account(account)
                .uid(uid)
                .build();
    }
}

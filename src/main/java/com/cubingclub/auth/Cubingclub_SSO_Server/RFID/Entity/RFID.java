package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Entity;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFID {
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Id
    private String uid;
}
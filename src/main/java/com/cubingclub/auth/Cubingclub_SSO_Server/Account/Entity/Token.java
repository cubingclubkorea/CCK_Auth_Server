package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String invalidRefreshToken;
    public Token(String invalidRefreshToken){
        this.invalidRefreshToken=invalidRefreshToken;
    }
}

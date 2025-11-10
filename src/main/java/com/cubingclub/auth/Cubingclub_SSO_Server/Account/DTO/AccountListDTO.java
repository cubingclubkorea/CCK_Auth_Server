package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;


import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Gender;

public interface AccountListDTO {
    String getName();
    String getEnName();
    String getState();
    Gender getGender();
    String getCckId();
    String getWcaId();
}

package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PwReissueRequestDTO {
    @Email
    @NotEmpty
    private String email;
    private String authNum;
}

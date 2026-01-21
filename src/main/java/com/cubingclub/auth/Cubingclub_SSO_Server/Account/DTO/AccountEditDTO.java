package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountEditDTO {
    @NotBlank
    private String password;

    private String newPassword;

    private String newPasswordCheck;

    private String state;
}

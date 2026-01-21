package com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateDTO {
    @NotBlank
    private String cckId;

    @NotNull
    private UserRole position;
}

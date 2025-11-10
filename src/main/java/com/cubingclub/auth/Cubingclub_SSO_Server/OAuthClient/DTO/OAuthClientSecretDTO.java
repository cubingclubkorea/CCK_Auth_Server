package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthClientSecretDTO {
    private String clientId;
    private Integer expireDay;
}
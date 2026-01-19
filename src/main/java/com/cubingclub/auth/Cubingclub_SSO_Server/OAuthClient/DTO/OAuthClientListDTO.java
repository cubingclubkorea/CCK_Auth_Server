package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClientListDTO {
    List<OAuthClientInfoDTO> confirmed=new ArrayList<>();
    List<OAuthClientInfoDTO> waiting=new ArrayList<>();
}

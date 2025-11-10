package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
@Builder
@AllArgsConstructor
public class OAuthScopes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column
    private Long idx;
    @ManyToOne
    @JsonIgnore
    @JoinColumn
    private OAuthClient oAuthClient;
    @Column
    private String scope;
}

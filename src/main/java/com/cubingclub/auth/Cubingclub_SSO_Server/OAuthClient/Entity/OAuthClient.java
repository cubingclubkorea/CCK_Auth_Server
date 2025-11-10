package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table
@Builder
@AllArgsConstructor
public class OAuthClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column(unique = true)
    private String clientId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientSecret;
    @ManyToOne
    @JoinColumn
    private Account clientOwner;
    @Column
    private String clientName;
    @Column
    private String clientUri;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientDescription;
    @Column
    private String redirectUri;
    @Column
    private String postLogoutRedirectUri;
    @Builder.Default
    @OneToMany(mappedBy = "oAuthClient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OAuthScopes> scopes = new ArrayList<>();
    @Column
    private boolean requireAuthorizationConsent;
    @Enumerated(EnumType.STRING)
    @Column
    private ClientType clientType;  // 추가 필드
    @Column
    private LocalDateTime clientSecretExpiresAt;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime updatedAt;
    @Column
    private boolean isActive;
}

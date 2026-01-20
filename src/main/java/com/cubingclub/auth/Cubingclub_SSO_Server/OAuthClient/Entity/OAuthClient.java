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
    private Long id;

    @Column(unique = true)
    private String clientId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientSecret;

    @ManyToOne
    private Account clientOwner;

    private String clientName;

    private String clientUri;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientDescription;

    private String redirectUri;

    private String postLogoutRedirectUri;

    @Builder.Default
    @OneToMany(mappedBy = "oauthClient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OAuthScopes> scopes = new ArrayList<>();

    private Boolean requireAuthorizationConsent;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;  // 추가 필드

    private LocalDateTime clientSecretExpiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    public void updateSecret(String encodedSecret, LocalDateTime expiresAt) {
        this.clientSecret = encodedSecret;
        this.clientSecretExpiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleActive() {
        this.isActive = !this.isActive;
        this.updatedAt = LocalDateTime.now();
    }
}

package com.cubingclub.auth.Cubingclub_SSO_Server.OAuthClient.Entity;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OAuthClient {
    @Id
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
    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    @Builder.Default
    private List<String> scopes = new ArrayList<>();
    private Boolean requireAuthorizationConsent;
    @Enumerated(EnumType.STRING)
    private ClientType clientType;  // 추가 필드
    private LocalDateTime clientSecretExpiresAt;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private Boolean isActive;

    public void addScope(String scope) {
        scopes.add(scope);
    }

    public void clearScopes() {
        scopes.clear();
    }

    @PreRemove
    private void preRemove() {
        clearScopes();
    }

    public void updateSecret(String encodedSecret, LocalDateTime expiresAt) {
        this.clientSecret = encodedSecret;
        this.clientSecretExpiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleActive() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
}

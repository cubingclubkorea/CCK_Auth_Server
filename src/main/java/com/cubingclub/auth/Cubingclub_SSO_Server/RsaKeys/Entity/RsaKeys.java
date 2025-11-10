package com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class RsaKeys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(nullable = false, unique = true)
    private String keyId;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String privateKey;
}

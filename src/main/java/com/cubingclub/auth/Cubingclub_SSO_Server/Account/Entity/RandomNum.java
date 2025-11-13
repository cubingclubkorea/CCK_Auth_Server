package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class RandomNum {
    @Id
    @Column(length=7)
    private String randNum;
    @Column(unique = true, nullable = false)
    private String email;
    @Column
    private LocalDateTime expire;
}
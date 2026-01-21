package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    private String email;
    private String name;
    private String enName;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole position;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @Column(length = 20)
    private String state;
    private Gender gender;
    @Column(unique = true)
    private String cckId;
    private String wcaId;
    private String phoneNum;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void updateRole(UserRole newRole) {
        this.position = newRole;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateState(String newState) {
        this.state = newState;
    }
}
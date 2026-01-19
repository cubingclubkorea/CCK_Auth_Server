package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Account {
    @Id
    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String enName;
    @Column
    @JsonIgnore
    private String password;
    @Column
    @Enumerated(EnumType.STRING)
    private UserRole position;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @Column(length = 15)
    private String state;
    @Column
    private Gender gender;
    @Column(unique = true)
    private String cckId;
    @Column
    private String wcaId;
    @Column
    private String phoneNum;

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
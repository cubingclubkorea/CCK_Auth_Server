package com.cubingclub.auth.Cubingclub_SSO_Server.Config;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomUserService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByCckId(username)
                .orElseGet(() -> accountRepository.findByEmail(username.toUpperCase())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
        return org.springframework.security.core.userdetails.User
                .withUsername(account.getCckId())
                .password(account.getPassword())
                .roles(account.getPosition().name())
                .build();
    }
}

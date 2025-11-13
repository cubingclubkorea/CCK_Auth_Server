package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.RandomNum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RandomNumRepository extends JpaRepository<RandomNum, Integer> {
    RandomNum findRandomNumByRandNum(String randNum);
    RandomNum findRandomNumByEmail(String email);
    List<RandomNum> findRandomNumByExpireBefore(LocalDateTime now);
    void deleteByEmail(String email);
}

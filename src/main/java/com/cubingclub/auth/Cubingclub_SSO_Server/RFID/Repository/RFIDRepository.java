package com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Repository;

import com.cubingclub.auth.Cubingclub_SSO_Server.RFID.Entity.RFID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RFIDRepository extends JpaRepository<RFID, Integer> {
    @Query("select r.account.email from RFID r where r.uid=:uid")
    String findEmailByUid(@Param(value = "uid")String uid);
    
    @Query("select r from RFID r join fetch r.account where r.uid = :uid")
    Optional<RFID> findByUidWithAccount(@Param("uid") String uid);
    
    Optional<RFID> findByUid(String uid);
    void deleteByAccount_email(String email);
}

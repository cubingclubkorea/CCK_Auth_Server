package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.DTO.AccountListDTO;
import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Boolean existsByEmail(String email);
    Optional<Account> findByEmail(String email);
    Optional<Account> findByCckId(String cckId);
    @Query("SELECT a.name as name, a.enName as enName, a.state as state, a.gender as gender, a.cckId as cckId, a.wcaId as wcaId FROM Account a " +
            "where a.name like %:searchTerm% or a.enName like %:searchTerm% or a.cckId like %:searchTerm%")
    List<AccountListDTO> findByName(@Param(value="searchTerm")String searchTerm);
    @Modifying
    @Query("UPDATE Account a SET a.state=:state WHERE a.cckId=:cckId")
    void updateAccountInfo(@Param(value = "state")String state, @Param(value = "cckId")String cckId);
    @Modifying
    @Query("UPDATE Account a SET a.password=:password WHERE a.cckId=:cckId")
    void updateAccountPassword(@Param(value="password")String password, @Param(value = "cckId")String cckId);

    @Query("select cast(substring(a.cckId, length(:searchTerm)+1,2) as int) from Account a where a.cckId like concat(:searchTerm, '%') order by cast(substring(a.cckId, length(:searchTerm)+1,2) as int) desc limit 1")
    Optional<Integer> findBiggestNum(@Param(value="searchTerm")String searchTerm);

    long countByCckIdStartingWith(String prefix);
}

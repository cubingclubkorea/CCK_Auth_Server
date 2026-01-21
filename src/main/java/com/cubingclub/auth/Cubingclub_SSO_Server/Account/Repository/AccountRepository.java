package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Repository;

import com.cubingclub.auth.Cubingclub_SSO_Server.Account.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByCckId(String cckId);

    @Query("SELECT a FROM Account a WHERE a.name LIKE %:searchTerm% OR a.enName LIKE %:searchTerm% OR a.cckId LIKE %:searchTerm%")
    List<Account> findBySearchTerm(@Param("searchTerm") String searchTerm);

    @Query("select cast(substring(a.cckId, length(:searchTerm)+1,2) as int) from Account a where a.cckId like concat(:searchTerm, '%') order by cast(substring(a.cckId, length(:searchTerm)+1,2) as int) desc limit 1")
    Optional<Integer> findBiggestNum(@Param(value="searchTerm")String searchTerm);
}

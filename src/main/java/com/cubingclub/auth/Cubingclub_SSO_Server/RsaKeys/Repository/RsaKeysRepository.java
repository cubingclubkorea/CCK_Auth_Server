package com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Repository;

import com.cubingclub.auth.Cubingclub_SSO_Server.RsaKeys.Entity.RsaKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RsaKeysRepository extends JpaRepository<RsaKeys, Long> {
    Optional<RsaKeys> findByIdentifier(String identifier);
}

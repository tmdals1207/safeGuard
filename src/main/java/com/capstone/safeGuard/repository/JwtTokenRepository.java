package com.capstone.safeGuard.repository;

import com.capstone.safeGuard.domain.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByAccessToken(String accessToken);
}

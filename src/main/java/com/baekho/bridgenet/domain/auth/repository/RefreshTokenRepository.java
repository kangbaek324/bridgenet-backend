package com.baekho.bridgenet.domain.auth.repository;

import com.baekho.bridgenet.domain.auth.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
}

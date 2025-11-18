package com.baekho.bridgenet.global.common.repository;

import com.baekho.bridgenet.global.common.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
}

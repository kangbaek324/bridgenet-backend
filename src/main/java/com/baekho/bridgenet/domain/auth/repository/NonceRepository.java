package com.baekho.bridgenet.domain.auth.repository;

import com.baekho.bridgenet.domain.auth.entity.Nonces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonceRepository extends JpaRepository<Nonces, String> {
    boolean existsByNonce(String nonce);
    Nonces findByAddress(String address);
}

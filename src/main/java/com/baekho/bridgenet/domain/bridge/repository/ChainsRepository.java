package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.bridge.entity.Chains;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChainsRepository extends JpaRepository<Chains, Long> {
    Optional<Chains> findByChainId(Long ChainId);
}

package com.baekho.bridgenet.domain.chain.repository;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChainRepository extends JpaRepository<Chain, Long> {
    Optional<Chain> findByChainId(Long ChainId);
    List<Chain> findAllByStatus(Boolean status);
}

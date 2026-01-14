package com.baekho.bridgenet.domain.chain.repository;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.projection.ChainStatusProjection;
import com.baekho.bridgenet.global.common.enums.ChainStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChainRepository extends JpaRepository<Chain, Long> {
    Optional<Chain> findByChainId(Long ChainId);
    boolean existsByChainId(Long id);
    boolean existsByChainName(String chainName);
    List<Chain> findAllByStatus(ChainStatus status);
    Optional<ChainStatusProjection> findStatusByChainId(Long chainId);
}

package com.baekho.bridgenet.domain.chain.repository;

import com.baekho.bridgenet.domain.chain.dto.ChainCountDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RpcRepository extends JpaRepository<Rpc, Long> {
    Optional<List<Rpc>> findAllByChain(Chain chain);

    @Query("SELECT new com.baekho.bridgenet.domain.chain.dto.ChainCountDTO(r.chain.chainId, COUNT(r)) " +
            "FROM Rpc r " +
            "GROUP BY r.chain.chainId")
    List<ChainCountDTO> countByChainId();
}

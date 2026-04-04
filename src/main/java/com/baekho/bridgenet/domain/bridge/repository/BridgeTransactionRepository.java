package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BridgeTransactionRepository extends JpaRepository<BridgeTransaction, Long> {
    @Query("SELECT t FROM BridgeTransaction t WHERE t.type = 'FROM' AND t.chain = :chain AND t.processedBlock <= :confirmedBlock AND t.status = 'PENDING'")
    List<BridgeTransaction> findConfirmedByChainAndType(@Param("chain") Chain chain, @Param("confirmedBlock") BigInteger confirmedBlock);
}

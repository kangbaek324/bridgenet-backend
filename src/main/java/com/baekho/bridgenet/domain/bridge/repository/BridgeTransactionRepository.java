package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import com.baekho.bridgenet.global.common.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface BridgeTransactionRepository extends JpaRepository<BridgeTransaction, Long> {
    int countByExchangeRequestAndStatus(ExchangeRequest exchangeRequest, TransactionStatus status);

    @Query("SELECT t FROM BridgeTransaction t WHERE t.type = :type AND t.chain = :chain AND t.processedBlock <= :confirmedBlock AND t.status = 'PENDING'")
    List<BridgeTransaction> findConfirmedByChainAndType(
            @Param("chain") Chain chain,
            @Param("confirmedBlock") BigInteger confirmedBlock,
            @Param("type") TransactionType type
    );

    List<BridgeTransaction> findByExchangeRequestAndType(ExchangeRequest exchangeRequest, TransactionType transactionType);
}

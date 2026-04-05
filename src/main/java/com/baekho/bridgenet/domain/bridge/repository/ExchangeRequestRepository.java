package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long>, JpaSpecificationExecutor<ExchangeRequest> {
    List<ExchangeRequest> findAllByUser(User users);

    @Query("""
        SELECT e FROM ExchangeRequest e
        WHERE e.bridgeStatus = 'IN_PROGRESS'
        AND e.approveStatus = 'APPROVE'
        AND NOT EXISTS (
            SELECT t FROM BridgeTransaction t
            WHERE t.exchangeRequest = e AND t.type = 'TO'
        )
    """)
    List<ExchangeRequest> findPendingRelayRequests();
    List<ExchangeRequest> findAllByApproveStatus(RequestStatus status);

    @Query("""
    SELECT e.toChain.chainId, e.toChain.chainName, SUM(e.toValue), e.toChain.unit
    FROM ExchangeRequest e
    WHERE e.approveStatus = :status
    GROUP BY e.toChain.chainId, e.toChain.chainName
    ORDER BY SUM(e.toValue) DESC
    """)
    List<List<Object>> findTotalToValueByChain(@Param("status") RequestStatus status);

    @Query("""
    SELECT e.fromChain.chainId, e.fromChain.chainName, SUM(e.fromValue), e.fromChain.unit
    FROM ExchangeRequest e
    WHERE e.approveStatus = :status
    GROUP BY e.fromChain.chainId, e.fromChain.chainName
    ORDER BY SUM(e.fromValue) DESC
    """)
    List<List<Object>> findTotalFromValueByChain(@Param("status") RequestStatus status);
}


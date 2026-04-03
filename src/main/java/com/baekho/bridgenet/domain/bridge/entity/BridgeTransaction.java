package com.baekho.bridgenet.domain.bridge.entity;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import com.baekho.bridgenet.global.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "bridge_transactions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BridgeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exchange_request_id", nullable = false)
    private ExchangeRequest exchangeRequest;

    @ManyToOne
    @JoinColumn(name = "chain_id", nullable = false)
    private Chain chain;

    @Column(name = "transaction_hash", nullable = false)
    private String transactionHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "processed_block")
    private BigInteger ProcessedBlock;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}

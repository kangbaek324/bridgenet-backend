package com.baekho.bridgenet.domain.bridge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "chains")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chains {
    @Id
    @Column(name = "chain_id", nullable = false)
    private Long chainId;

    @Column(name = "chain_name", nullable = false)
    private String chainName;

    @Column(name = "smart_contract_address", nullable = false)
    private String smartContractAddress;

    @Column(name = "smart_contract_value", nullable = false)
    private Long smartContractValue;

    @Column(name = "http_rpc", nullable = false)
    private String httpRpc;

    @Column(name = "ws_rpc", nullable = false)
    private String wsRpc;

    @Column(name = "last_block_number", nullable = false)
    private BigInteger lastBlockNumber;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

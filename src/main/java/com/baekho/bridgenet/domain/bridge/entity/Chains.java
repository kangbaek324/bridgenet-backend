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
    Long chainId;

    @Column(name = "chain_name", nullable = false)
    String chainName;

    @Column(name = "smart_contract_address", nullable = false)
    String smartContractAddress;

    @Column(name = "smart_contract_value", nullable = false)
    Long smartContractValue;

    @Column(name = "http_rpc", nullable = false)
    String httpRpc;

    @Column(name = "ws_rpc", nullable = false)
    String wsRpc;

    @Column(name = "last_block_number", nullable = false)
    BigInteger lastBlockNumber;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    LocalDateTime updatedAt;
}

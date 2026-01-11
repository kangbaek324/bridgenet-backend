package com.baekho.bridgenet.domain.chain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chains")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chain_id", nullable = false, unique = true)
    private Long chainId;

    @Column(name = "chain_name", nullable = false, unique = true)
    private String chainName;

    @Column(name = "smart_contract_address", nullable = false)
    private String smartContractAddress;

    @Builder.Default
    @Column(name = "smart_contract_value", nullable = false)
    private BigInteger smartContractValue = BigInteger.valueOf(0);

    @Column(name = "unit")
    private String unit;

    @Builder.Default
    @Column(nullable = false)
    private boolean status = false;

    @Column(name = "last_block_number", nullable = false)
    private BigInteger lastBlockNumber;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "chain",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Rpc> rpcs = new ArrayList<>();
}

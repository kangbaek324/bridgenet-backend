package com.baekho.bridgenet.domain.bridge.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chain_id")
    Long chainId;

    @Column(name = "chain_name")
    String chainName;

    @Column(name = "smartcontract_address")
    String smartContractAddress;

    @Column(name = "smartcontract_value")
    Long smartContractValue;

    @Column(name = "http_rpc")
    String httpRpc;

    @Column(name = "ws_rpc")
    String wsRpc;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

}

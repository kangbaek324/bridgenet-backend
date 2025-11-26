package com.baekho.bridgenet.domain.bridge.entity;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_request")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "id_in_smart_contract")
    BigInteger idInSmartContract;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users user;

    @ManyToOne()
    @JoinColumn(name = "from_chain_id", nullable = false)
    Chains fromChain;

    @Column(name = "from_value", nullable = false)
    BigInteger fromValue;

    @ManyToOne()
    @JoinColumn(name = "to_chain_id", nullable = false)
    Chains toChain;

    @Column(name = "to_value", nullable = false)
    BigInteger toValue;

    @Column(name = "approve_status")
    RequestStatus approveStatus;

    @Column(name = "transaction_hash")
    String transactionHash;

    @ManyToOne()
    @JoinColumn(name = "approve_user_id")
    Users approveUser;

    @Column(name = "approved_at")
    LocalDateTime approvedAt;
}

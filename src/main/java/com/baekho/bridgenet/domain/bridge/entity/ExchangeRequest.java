package com.baekho.bridgenet.domain.bridge.entity;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "exchange_requests",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_chain_id", "id_in_smart_contract"})
    }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_in_smart_contract")
    private BigInteger idInSmartContract;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne()
    @JoinColumn(name = "from_chain_id", nullable = false)
    private Chain fromChain;

    @Column(name = "from_value", nullable = false)
    private BigInteger fromValue;

    @ManyToOne()
    @JoinColumn(name = "to_chain_id", nullable = false)
    private Chain toChain;

    @Column(name = "to_value", nullable = false)
    private BigInteger toValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status")
    private RequestStatus approveStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "bridge_status", nullable = false, length = 20)
    private BridgeStatus bridgeStatus;

    @ManyToOne()
    @JoinColumn(name = "approve_user_id")
    private Users approveUser;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
}

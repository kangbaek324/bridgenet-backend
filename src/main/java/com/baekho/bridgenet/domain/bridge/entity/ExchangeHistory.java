package com.baekho.bridgenet.domain.bridge.entity;

import com.baekho.bridgenet.domain.auth.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    Users user;

    @ManyToOne
    @JoinColumn(name = "from_chain_id", nullable = false)
    Chains fromChain;

    @Column(name = "from_value", nullable = false)
    BigInteger fromValue;

    @ManyToOne
    @JoinColumn(name = "to_chain_id", nullable = false)
    Chains toChain;

    @Column(name = "to_value", nullable = false)
    BigInteger toValue;

    @Column(name = "exchanged_at", nullable = false)
    LocalDateTime exchangedAt;
}

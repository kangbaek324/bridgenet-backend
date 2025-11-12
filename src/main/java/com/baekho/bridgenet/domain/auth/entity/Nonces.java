package com.baekho.bridgenet.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nonces")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Nonces {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String address;

    @Column(nullable = false, unique = true)
    String nonce;

    @Column(nullable = false, name = "expiry_date")
    LocalDateTime expiryDate;
}

package com.baekho.bridgenet.global.common.entity;

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
    private Long id;

    @Column(nullable = false, unique = true)
    private String address;

    @Column(nullable = false, unique = true)
    private String nonce;

    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;
}

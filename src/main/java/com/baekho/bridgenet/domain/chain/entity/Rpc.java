package com.baekho.bridgenet.domain.chain.entity;

import com.baekho.bridgenet.global.common.enums.Protocol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rpcs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rpc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chain_id")
    private Chain chain;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Protocol protocol;

    @Column(nullable = false, unique = true)
    private String url;
}

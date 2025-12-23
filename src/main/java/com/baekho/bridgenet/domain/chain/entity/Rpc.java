package com.baekho.bridgenet.domain.chain.entity;

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

    @Column()
    private String http;

    @Column()
    private String ws;
}

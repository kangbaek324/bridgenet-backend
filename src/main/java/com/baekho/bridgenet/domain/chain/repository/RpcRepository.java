package com.baekho.bridgenet.domain.chain.repository;

import com.baekho.bridgenet.domain.chain.entity.Rpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpcRepository extends JpaRepository<Rpc, Long> {
}

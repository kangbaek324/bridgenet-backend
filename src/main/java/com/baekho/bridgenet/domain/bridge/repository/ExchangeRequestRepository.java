package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {
}

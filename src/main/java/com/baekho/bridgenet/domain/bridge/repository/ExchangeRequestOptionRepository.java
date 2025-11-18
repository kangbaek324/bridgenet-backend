package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRequestOptionRepository extends JpaRepository<ExchangeRequestOption, Long> {
}

package com.baekho.bridgenet.domain.bridge.repository;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, Long> {
    List<ExchangeHistory> findAllByUser(Users user);
}

package com.baekho.bridgenet.domain.auth.repository;

import com.baekho.bridgenet.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByAddress(String address);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByAddress(String address);
}

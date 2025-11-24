package com.baekho.bridgenet.domain.auth.repository;

import com.baekho.bridgenet.domain.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByAddress(String address);
    boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByAddress(String address);
}

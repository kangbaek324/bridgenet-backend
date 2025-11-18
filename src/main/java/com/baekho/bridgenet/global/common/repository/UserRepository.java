package com.baekho.bridgenet.global.common.repository;

import com.baekho.bridgenet.global.common.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByAddress(String address);
    boolean existsByUsername(String username);
    Optional<Users> findByUsername(String username);
}

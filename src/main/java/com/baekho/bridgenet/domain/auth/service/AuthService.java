package com.baekho.bridgenet.domain.auth.service;

import com.baekho.bridgenet.domain.auth.dto.NonceRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.NonceResponseDTO;
import com.baekho.bridgenet.domain.auth.entity.Nonces;
import com.baekho.bridgenet.domain.auth.repository.AuthRepository;
import com.baekho.bridgenet.domain.auth.repository.NonceRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final NonceRepository nonceRepository;

    public NonceResponseDTO getNonce(NonceRequestDTO dto) {
        Random random = new Random();
        String nonceValue;

        // 중복 검사
        while(true) {
            nonceValue =
                    Integer.toString(random.nextInt(10000000)) +
                    Integer.toString(random.nextInt(10000000));

            if (!nonceRepository.existsByNonce(nonceValue)) break;
        }

        Nonces existing = nonceRepository.findByAddress(dto.getAddress());

        // 있으면 업데이트 없으면 생성
        if (existing == null) {
            Nonces nonce = Nonces.builder()
                    .address(dto.getAddress())
                    .nonce(nonceValue)
                    .expiryDate(LocalDateTime.now().plusMinutes(5))
                    .build();

            nonceRepository.save(nonce);
        }
        else {
            existing.setNonce(nonceValue);
            existing.setExpiryDate(LocalDateTime.now().plusMinutes(5));

            nonceRepository.save(existing);
        }

        return new NonceResponseDTO(nonceValue);
    }
}

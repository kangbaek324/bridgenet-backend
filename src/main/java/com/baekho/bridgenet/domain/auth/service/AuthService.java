package com.baekho.bridgenet.domain.auth.service;

import com.baekho.bridgenet.domain.auth.dto.NonceRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.NonceResponseDTO;
import com.baekho.bridgenet.domain.auth.dto.RegisterRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.RegisterResponseDTO;
import com.baekho.bridgenet.domain.auth.entity.Nonces;
import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.enums.Role;
import com.baekho.bridgenet.domain.auth.repository.AuthRepository;
import com.baekho.bridgenet.domain.auth.repository.NonceRepository;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final NonceRepository nonceRepository;

    public NonceResponseDTO getNonce(NonceRequestDTO dto) {
        Random random = new Random();
        String nonceValue;
        String address = dto.getAddress().toLowerCase();

        // 중복 검사
        while(true) {
            nonceValue =
                    Integer.toString(random.nextInt(10000000)) +
                    Integer.toString(random.nextInt(10000000));

            if (!nonceRepository.existsByNonce(nonceValue)) break;
        }

        Nonces existing = nonceRepository.findByAddress(address);

        // 있으면 업데이트 없으면 생성
        if (existing == null) {
            Nonces nonce = Nonces.builder()
                    .address(address)
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

    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        Nonces nonce = nonceRepository.findByAddress(dto.getAddress());
        if (nonce.getExpiryDate().isBefore(LocalDateTime.now())) {
            nonceRepository.delete(nonce);
            throw new AuthException(AuthErrorCode.NONCE_EXPIRED_DATE);
        }
        String message = "Welcome to Bridgenet !\n\nLogin With " + nonce.getNonce();

        // prefix 붙이고 붙인 바이트값을 해쉬화 해서 줌
        byte[] messageHash = Sign.getEthereumMessageHash(message.getBytes(StandardCharsets.UTF_8));

        // 받은 싸인된 값을 바이트로 만듬 (메타마스크는 싸인값을 hex로 주기때문)
        byte[] signBytes = Numeric.hexStringToByteArray(dto.getSignatureData());

        // 서명바이트가 65자가 아닐경우에는 잘못된것임
        if (signBytes.length != 65) throw new IllegalArgumentException("잘못된 서명값 입니다.");

        // R S V 값 추츨
        byte[] r = Arrays.copyOfRange(signBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signBytes, 32, 64);
        byte v = signBytes[64];

        // 0, 1일경우를 27, 28로 보정
        if (v < 27) v += 27;

        // 싸인값 객체로 생성
        Sign.SignatureData sigData = new Sign.SignatureData(v, r, s);
        BigInteger publicKey;

        try {
            // 해쉬화된 메세지값과 이를 서명한 값을 이용한 publickKey 복구
            publicKey = Sign.signedMessageHashToKey(messageHash, sigData);
        } catch (SignatureException e) {
            throw new AuthException(AuthErrorCode.INCORRECT_SIGNATURE);
        }

        // 주소 복구
        String recoveredAddress = "0x" + Keys.getAddress(publicKey);

        if (recoveredAddress.equals(dto.getAddress())) {
            if (authRepository.existsByAddress(dto.getAddress())) throw new AuthException(AuthErrorCode.ADDRESS_ALREADY_EXISTS);
            else if (authRepository.existsByUsername(dto.getUsername())) throw new AuthException(AuthErrorCode.USER_NAME_ALREADY_EXISTS);

            Users user = Users.builder()
                    .address(dto.getAddress())
                    .username(dto.getUsername())
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .deletedAt(null)
                    .build();

            authRepository.save(user);
            
            return new RegisterResponseDTO(dto.getUsername(), dto.getAddress());
        }
        else {
            throw new AuthException(AuthErrorCode.INCORRECT_SIGNATURE);
        }
    }
}

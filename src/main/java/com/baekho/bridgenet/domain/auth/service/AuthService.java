package com.baekho.bridgenet.domain.auth.service;

import com.baekho.bridgenet.domain.auth.dto.*;
import com.baekho.bridgenet.global.auth.TokenProvider;
import com.baekho.bridgenet.global.common.entity.Nonces;
import com.baekho.bridgenet.global.common.entity.RefreshTokens;
import com.baekho.bridgenet.global.common.entity.Users;
import com.baekho.bridgenet.global.common.enums.Role;
import com.baekho.bridgenet.global.common.repository.NonceRepository;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.global.common.repository.RefreshTokenRepository;
import com.baekho.bridgenet.global.common.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final NonceRepository nonceRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;

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

    private String signedMessageToAddress(String signedMessage, String message) {
        // prefix 붙이고 붙인 바이트값을 해쉬화 해서 줌
        byte[] messageHash = Sign.getEthereumMessageHash(message.getBytes(StandardCharsets.UTF_8));

        // 받은 싸인된 값을 바이트로 만듬 (메타마스크는 싸인값을 hex로 주기때문)
        byte[] signBytes = Numeric.hexStringToByteArray(signedMessage);

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
        return "0x" + Keys.getAddress(publicKey);
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO dto) {
        Nonces nonce = nonceRepository.findByAddress(dto.getAddress());
        if (nonce.getExpiryDate().isBefore(LocalDateTime.now())) {
            nonceRepository.delete(nonce);
            throw new AuthException(AuthErrorCode.NONCE_EXPIRED_DATE);
        }
        String message = "Welcome to Bridgenet !\n\nRegister With " + nonce.getNonce();
        String recoveredAddress = signedMessageToAddress(dto.getSignatureData(), message);

        if (recoveredAddress.equals(dto.getAddress())) {
            if (userRepository.existsByAddress(dto.getAddress())) throw new AuthException(AuthErrorCode.ADDRESS_ALREADY_EXISTS);
            else if (userRepository.existsByUsername(dto.getUsername())) throw new AuthException(AuthErrorCode.USERNAME_ALREADY_EXISTS);

            Users user = Users.builder()
                    .address(dto.getAddress())
                    .username(dto.getUsername())
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .deletedAt(null)
                    .build();

            userRepository.save(user);
            
            return new RegisterResponseDTO(dto.getUsername(), dto.getAddress());
        }
        else {
            throw new AuthException(AuthErrorCode.INCORRECT_SIGNATURE);
        }
    }

    public LoginResponseDTO login(LoginRequestDTO dto, HttpServletResponse response) {
        Users user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNKNOWN_USERNAME));

        Nonces nonce = nonceRepository.findByAddress(user.getAddress());
        if (nonce.getExpiryDate().isBefore(LocalDateTime.now())) {
            nonceRepository.delete(nonce);
            throw new AuthException(AuthErrorCode.NONCE_EXPIRED_DATE);
        }

        String message = "Welcome to Bridgenet !\n\nLogin With " + nonce.getNonce();
        String recoveredAddress = signedMessageToAddress(dto.getSignatureData(), message);

        String accessToken = tokenProvider.createToken(user.getId());
        String refreshToken = tokenProvider.createRefreshToken(user.getId());

        RefreshTokens refreshTokenDB = RefreshTokens.builder()
                .refreshToken(refreshToken)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();

        refreshTokenRepository.save(refreshTokenDB);

        ResponseCookie cookie = ResponseCookie.from("refreshTokenId", user.getId().toString())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return new LoginResponseDTO(accessToken);
    }

    public RefreshAccessTokenResponseDTO refreshAccessToken(String refreshTokenId) {
        if (refreshTokenId == null) throw new AuthException(AuthErrorCode.REFRESH_TOKEN_ID_IS_NULL);
        RefreshTokens refreshTokens = refreshTokenRepository.findById(Long.parseLong(refreshTokenId))
                .orElseThrow(() -> new  AuthException(AuthErrorCode.REFRESH_TOKEN_NOTFOUND));

        if (refreshTokens.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshTokens);

            throw new AuthException(AuthErrorCode.REFERSH_TOKEN_EXPIRED);
        }

        String userId = tokenProvider.getUserId(refreshTokens.getRefreshToken());
        String accessToken = tokenProvider.createToken(Long.parseLong(userId));

        return new RefreshAccessTokenResponseDTO(accessToken);
    }

}

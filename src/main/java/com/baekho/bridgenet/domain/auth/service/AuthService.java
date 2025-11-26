package com.baekho.bridgenet.domain.auth.service;

import com.baekho.bridgenet.domain.auth.dto.*;
import com.baekho.bridgenet.global.auth.TokenProvider;
import com.baekho.bridgenet.domain.auth.entity.Nonces;
import com.baekho.bridgenet.domain.auth.entity.RefreshTokens;
import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.global.common.enums.Role;
import com.baekho.bridgenet.domain.auth.repository.NonceRepository;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.domain.auth.repository.RefreshTokenRepository;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
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
        do {
            nonceValue =
                    Integer.toString(random.nextInt(10000000)) +
                    Integer.toString(random.nextInt(10000000));

        } while (nonceRepository.existsByNonce(nonceValue));

        Optional<Nonces> existing = nonceRepository.findByAddress(address);
        Nonces nonceDB;

        if (existing.isEmpty()) {
            nonceDB = Nonces.builder()
                    .address(dto.getAddress())
                    .nonce(nonceValue)
                    .expiryDate(LocalDateTime.now().plusMinutes(5))
                    .build();
        }
        else {
            nonceDB = existing.get();

            nonceDB.setNonce(nonceValue);
            nonceDB.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        }

        nonceRepository.save(nonceDB);

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
        Nonces nonce = nonceRepository.findByAddress(dto.getAddress())
                .orElseThrow(() -> new AuthException(AuthErrorCode.NONCE_NOT_FOUND));

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
                    .deletedAt(null)
                    .build();

            userRepository.save(user);

            // 사용한 논스값 삭제
            nonceRepository.delete(nonce);
            
            return new RegisterResponseDTO(dto.getUsername(), dto.getAddress());
        }
        else {
            throw new AuthException(AuthErrorCode.INCORRECT_SIGNATURE);
        }
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto, HttpServletResponse response) {
        Users user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNKNOWN_USER));

        Nonces nonce = nonceRepository.findByAddress(user.getAddress())
                .orElseThrow(() -> new AuthException(AuthErrorCode.NONCE_NOT_FOUND));

        if (nonce.getExpiryDate().isBefore(LocalDateTime.now())) {
            nonceRepository.delete(nonce);
            throw new AuthException(AuthErrorCode.NONCE_EXPIRED_DATE);
        }

        String message = "Welcome to Bridgenet !\n\nLogin With " + nonce.getNonce();
        String recoveredAddress = signedMessageToAddress(dto.getSignatureData(), message);

        if (!user.getAddress().equals(recoveredAddress)) {
            throw new AuthException(AuthErrorCode.INCORRECT_USERINFO);
        }

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

        // 사용한 논스 삭제
        nonceRepository.delete(nonce);

        return new LoginResponseDTO(accessToken);
    }

    public RefreshAccessTokenResponseDTO refreshAccessToken(String refreshTokenId) {
        if (refreshTokenId == null) throw new AuthException(AuthErrorCode.REFRESH_TOKEN_ID_IS_NULL);
        RefreshTokens refreshTokens = refreshTokenRepository.findById(Long.parseLong(refreshTokenId))
                .orElseThrow(() -> new  AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (refreshTokens.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshTokens);

            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String userId = tokenProvider.getUserId(refreshTokens.getRefreshToken());
        String accessToken = tokenProvider.createToken(Long.parseLong(userId));

        return new RefreshAccessTokenResponseDTO(accessToken);
    }

    public boolean isAdmin(Users user) {
        return user.getRole() == Role.ADMIN;
    }
}

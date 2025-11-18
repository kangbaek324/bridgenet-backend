package com.baekho.bridgenet.domain.auth.controller;

import com.baekho.bridgenet.domain.auth.dto.*;
import com.baekho.bridgenet.domain.auth.service.AuthService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("nonce")
    public ResponseEntity<SuccessResponse<NonceResponseDTO>> getNonce(
            @Valid @RequestBody NonceRequestDTO dto
    ) {
        NonceResponseDTO result = authService.getNonce(dto);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("register")
    public ResponseEntity<SuccessResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {
        RegisterResponseDTO result = authService.register(dto);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("login")
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletResponse response
    ) {
        LoginResponseDTO result = authService.login(dto, response);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("refresh")
    public ResponseEntity<SuccessResponse<RefreshAccessTokenResponseDTO>> refreshAccessToken(
            @CookieValue(name = "refreshTokenId", required = false) String refreshTokenId
    ) {
        RefreshAccessTokenResponseDTO result = authService.refreshAccessToken(refreshTokenId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

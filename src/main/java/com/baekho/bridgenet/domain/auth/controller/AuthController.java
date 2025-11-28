package com.baekho.bridgenet.domain.auth.controller;

import com.baekho.bridgenet.domain.auth.dto.request.LoginRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.request.NonceRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.request.RegisterRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.response.LoginResponseDTO;
import com.baekho.bridgenet.domain.auth.dto.response.NonceResponseDTO;
import com.baekho.bridgenet.domain.auth.dto.response.RefreshAccessTokenResponseDTO;
import com.baekho.bridgenet.domain.auth.dto.response.RegisterResponseDTO;
import com.baekho.bridgenet.domain.auth.service.AuthService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "논스값 요청", description = "서명메세지를 만들기 위한 논스값을 발급합니다.")
    @PostMapping("nonce")
    public ResponseEntity<SuccessResponse<NonceResponseDTO>> getNonce(
            @Valid @RequestBody NonceRequestDTO dto
    ) {
        NonceResponseDTO result = authService.getNonce(dto);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
    @PostMapping("register")
    public ResponseEntity<SuccessResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO dto
    ) {
        RegisterResponseDTO result = authService.register(dto);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "로그인", description = "서비스에 로그인을 요청합니다.")
    @PostMapping("login")
    public ResponseEntity<SuccessResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletResponse response
    ) {
        LoginResponseDTO result = authService.login(dto, response);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "엑세스토큰 재발급 요청", description = "엑세스토큰 재발급을 요청합니다.")
    @PostMapping("refresh")
    public ResponseEntity<SuccessResponse<RefreshAccessTokenResponseDTO>> refreshAccessToken(
            @CookieValue(name = "refreshTokenId", required = false) String refreshTokenId
    ) {
        RefreshAccessTokenResponseDTO result = authService.refreshAccessToken(refreshTokenId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

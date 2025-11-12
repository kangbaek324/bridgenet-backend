package com.baekho.bridgenet.domain.auth.controller;

import com.baekho.bridgenet.domain.auth.dto.NonceRequestDTO;
import com.baekho.bridgenet.domain.auth.dto.NonceResponseDTO;
import com.baekho.bridgenet.domain.auth.service.AuthService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

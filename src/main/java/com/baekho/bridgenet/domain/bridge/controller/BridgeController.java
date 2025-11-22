package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.dto.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bridge")
public class BridgeController {
    private final BridgeService bridgeService;
    private final UserRepository userRepository;

    @PostMapping("request-option")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> setRequestOption(
            @Valid @RequestBody RequestOptionSetRequestDTO dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        bridgeService.setRequestOptionStatus(dto, user);

        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

    @GetMapping("history/my")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getMyExchangeHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @GetMapping("history/{userId}")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getUserExchangeHistory(
            @PathVariable Long userId
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNKNOWN_USER));

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

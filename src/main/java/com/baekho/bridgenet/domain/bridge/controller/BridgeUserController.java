package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.auth.service.AuthService;
import com.baekho.bridgenet.domain.bridge.dto.response.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class BridgeUserController {
    private final AuthService authService;
    private final BridgeService bridgeService;

    @Operation(summary = "내 교환 기록 조회", description = "로그인 한 유저의 교환기록을 조회합니다.")
    @GetMapping("/me/exchange-requests")
    public ResponseEntity<SuccessResponse<Page<BridgeHistoryResponseDTO>>> getMyExchangeHistory(
            @RequestParam(name = "sort", defaultValue = "latest") String sortType,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "chainId", required = false) Long chainId,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "status", required = false) String status
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Page<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(
                sortType, size, page - 1, chainId, direction, status, user
        );

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "특정 유저 교환 기록 조회", description = "특정 유저의 교환기록을 조회합니다.")
    @GetMapping("/{userId}/exchange-requests")
    public ResponseEntity<SuccessResponse<Page<BridgeHistoryResponseDTO>>> getUserExchangeHistory(
            @RequestParam(name = "sort", defaultValue = "latest") String sortType,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "chainId", required = false) Long chainId,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "status", required = false) String status,
            @PathVariable Long userId
    ) {
        User user = authService.getUserByUserId(userId);

        Page<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(
                sortType, size, page - 1, chainId, direction, status, user
        );

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

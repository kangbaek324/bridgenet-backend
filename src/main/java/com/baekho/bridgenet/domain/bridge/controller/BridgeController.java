package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.dto.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @PostMapping("request/{id}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ExchangeApproveResponseDTO>> setRequest(
            @PathVariable Long id,
            @Valid @RequestBody ExchangeApproveRequestDTO dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        ExchangeApproveResponseDTO result = bridgeService.setRequest(dto, id, user);
        return ResponseEntity.ok(new SuccessResponse<>("" , result));
    }

    @GetMapping("history")
    public ResponseEntity<SuccessResponse<Page<BridgeHistoryResponseDTO>>> getExchangeHistory(
            @RequestParam(name = "sort", defaultValue = "latest") String sortType,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "chainId", required = false) Long chainId,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "status", required = false) String status
    ) {
        if (direction == null) direction = "";
        Page<BridgeHistoryResponseDTO> result = bridgeService.getExchangeAllHistory(sortType, size, page, chainId, direction, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @GetMapping("history/my")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getMyExchangeHistory(
            @RequestParam(value = "status", required = false) String status
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @GetMapping("history/{userId}")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getUserExchangeHistory(
            @RequestParam(value = "status", required = false) String status,
            @PathVariable Long userId
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.UNKNOWN_USER));

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

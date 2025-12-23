package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.dto.response.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.ExchangeApproveResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.code.AuthErrorCode;
import com.baekho.bridgenet.global.common.exception.AuthException;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "처리 옵션 설정", description = "스마트컨트랙트에서의 브릿지 요청을 감지하고 어떻게 처리할 것 인지 설정합니다.")
    @PostMapping("request/option")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> setRequestOption(
            @Valid @RequestBody RequestOptionSetRequestDTO dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        bridgeService.setRequestOptionStatus(dto, user);

        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

    @Operation(summary = "요청 처리", description = "들어온 요청을 수동으로 처리 합니다.")
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

    @Operation(summary = "전체 요청 조회", description = "전체 요청을 조회합니다.")
    @GetMapping("request/history")
    public ResponseEntity<SuccessResponse<Page<BridgeHistoryResponseDTO>>> getExchangeHistory(
            @RequestParam(name = "sort", defaultValue = "latest") String sortType,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "chainId", required = false) Long chainId,
            @RequestParam(name = "direction", required = false) String direction,
            @RequestParam(name = "status", required = false) String status
    ) {
        if (direction == null) direction = "";
        Page<BridgeHistoryResponseDTO> result = bridgeService.getExchangeAllHistory(sortType, size, page - 1, chainId, direction, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "내 교환 기록 조회", description = "로그인 한 유저의 교환기록을 조회합니다.")
    @GetMapping("request/history/my")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getMyExchangeHistory(
            @RequestParam(value = "status", required = false) String status
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "특정 유저 교환 기록 조회", description = "특정 유저의 교환기록을 조회합니다.")
    @GetMapping("request/history/{userId}")
    public ResponseEntity<SuccessResponse<List<BridgeHistoryResponseDTO>>> getUserExchangeHistory(
            @RequestParam(value = "status", required = false) String status,
            @PathVariable Long userId
    ) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_USER));

        List<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(user, status);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

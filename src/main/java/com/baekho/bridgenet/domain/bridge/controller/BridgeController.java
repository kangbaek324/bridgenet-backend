package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.auth.service.AuthService;
import com.baekho.bridgenet.domain.bridge.dto.request.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bridge")
public class BridgeController {
    private final BridgeService bridgeService;
    private final AuthService authService;

    @Operation(summary = "처리 옵션 설정", description = "스마트컨트랙트에서의 브릿지 요청을 감지하고 어떻게 처리할 것 인지 설정합니다.")
    @PostMapping("request/option")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> setRequestOption(
            @Valid @RequestBody RequestOptionSetRequestDTO dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        bridgeService.setRequestOptionStatus(dto, user);

        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

    @Operation(summary = "요청 처리", description = "들어온 요청을 수동으로 처리 합니다.")
    @PostMapping("request/{id}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> setRequest(
            @PathVariable Long id,
            @Valid @RequestBody ExchangeApproveRequestDTO dto
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        bridgeService.setRequest(dto, id, user);
        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

//    @Operation(summary = "전체 요청 조회", description = "전체 요청을 조회합니다.")
//    @GetMapping("request/history")
//    public ResponseEntity<SuccessResponse<Page<BridgeHistoryResponseDTO>>> getExchangeHistory(
//            @RequestParam(name = "sort", defaultValue = "latest") String sortType,
//            @RequestParam(name = "size", defaultValue = "10") int size,
//            @RequestParam(name = "page", defaultValue = "1") int page,
//            @RequestParam(name = "chainId", required = false) Long chainId,
//            @RequestParam(name = "direction", required = false) String direction,
//            @RequestParam(name = "status", required = false) String status
//    ) {
//        Page<BridgeHistoryResponseDTO> result = bridgeService.getExchangeHistory(
//                sortType, size, page - 1, chainId, direction, status, null
//        );
//
//        return ResponseEntity.ok(new SuccessResponse<>("", result));
//    }
}

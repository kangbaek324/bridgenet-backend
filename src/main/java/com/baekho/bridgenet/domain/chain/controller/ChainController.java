package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.chain.dto.request.AddContractBalanceRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.ChainAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.ChainUpdateRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.*;
import com.baekho.bridgenet.domain.chain.dto.response.*;
import com.baekho.bridgenet.domain.chain.service.ChainService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/chains")
public class ChainController {
    private final ChainService chainService;

    @Operation(summary = "체인 리스트 조회", description = "서비스에서 제공하는 체인의 리스트를 조회합니다.")
    @GetMapping
    public ResponseEntity<SuccessResponse<ChainListResponseDTO>> getChainList() {
        ChainListResponseDTO result = chainService.getChainList(true);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    // @TODO Status도 반환되도록 수정 해야됨
    @Operation(summary = "체인 리스트 조회 (비활성화 포함)", description = "서비스에서 제공하는 체인의 리스트를 조회합니다. (비활성화 포함)")
    @GetMapping("all")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainListResponseDTO>> getAllChainList() {
        ChainListResponseDTO result = chainService.getChainList(null);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "체인 조회", description = "체인을 상세하게 조회합니다")
    @GetMapping("{chainId}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<AdminChainDetailDTO>> getChain(
            @PathVariable Long chainId
    ) {
        AdminChainDetailDTO result = chainService.getChain(chainId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "체인 추가", description = "새로운 체인을 추가합니다.")
    @PostMapping
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainAddResponseDTO>> addChain(
            @Valid @RequestBody ChainAddRequestDTO dto
    ) {
        ChainAddResponseDTO result = chainService.addChain(dto);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "체인 정보 변경", description = "체인의 정보를 변경합니다.")
    @PutMapping("{chainId}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainUpdateResponseDTO>> updateChain(
            @Valid @RequestBody ChainUpdateRequestDTO dto,
            @PathVariable Long chainId
    ) {
        ChainUpdateResponseDTO result = chainService.updateChain(dto, chainId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "체인 삭제", description = "체인을 삭제합니다.")
    @DeleteMapping("{chainId}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> removeChain(
            @PathVariable Long chainId
    ) {
        chainService.removeChain(chainId);

        return ResponseEntity
                .status(204)
                .body(new SuccessResponse<>("", null));
    }

    @GetMapping("{chainId}/status")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainStatusResponseDTO>> getChainStatus(
            @PathVariable Long chainId
    ) {
        ChainStatusResponseDTO res = chainService.getChainStatus(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    // @TODO 비동기로 변경 후 조회 API 제공
    @Operation(summary = "체인 활성화", description = "체인을 활성화 합니다.")
    @PostMapping("{chainId}/activate")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> activateChain(
            @PathVariable Long chainId
    ) {
        chainService.activateChain(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

    // @TODO 비동기로 변경 후 조회 API 제공
    @Operation(summary = "체인 비활성화", description = "체인을 비활성화 합니다.")
    @PostMapping("{chainId}/deactivate")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> deActivateChain(
            @PathVariable Long chainId
    ) {
        chainService.deActivateChain(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }

    @Operation(summary = "체인 랭킹 조회", description = "자금 유입/유출순으로 랭킹을 조회합니다.")
    @GetMapping("/ranking")
    public ResponseEntity<SuccessResponse<List<ChainRankingResponseDTO>>> getChainRanking(
            @RequestParam(name = "sort", defaultValue = "in") String sort
    ) {
        List<ChainRankingResponseDTO> result = chainService.getChainRanking(sort);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "컨트랙트 코인양 조회", description = "컨트랙트에 남은 코인양을 조회합니다.")
    @GetMapping("{chainId}/contract/balance")
    public ResponseEntity<SuccessResponse<ContractBalanceGetResponseDTO>> getContractBalance(
            @PathVariable Long chainId
    ) {
        ContractBalanceGetResponseDTO result = chainService.getContractBalance(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @Operation(summary = "컨트랙트 코인 충전", description = "컨트랙트에 코인을 충전합니다")
    @PostMapping("{chainId}/contract/balance")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> addContractBalance(
            @Valid @RequestBody AddContractBalanceRequestDTO dto,
            @PathVariable Long chainId
    ) {
        if (dto.getBalance().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("값이 0이 될 수 없습니다.");

        chainService.addContractBalance(dto, chainId);
        return ResponseEntity.ok(new SuccessResponse<>("" , null));
    }

    @Operation(summary = "컨트랙트 화이트리스트 등록", description = "자본을 옮길 컨트랙트에 화이트리스트 등록 요청을 보냅니다.")
    @PostMapping("{chainId}/contract/whitelist")
    public ResponseEntity<SuccessResponse<WhiteListResponseDTO>> setWhiteList(
            @PathVariable Long chainId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        WhiteListResponseDTO result = chainService.setWhiteList(chainId, user);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

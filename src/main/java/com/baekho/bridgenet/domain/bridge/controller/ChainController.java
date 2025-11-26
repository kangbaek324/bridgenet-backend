package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.bridge.dto.*;
import com.baekho.bridgenet.domain.bridge.service.ChainService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bridge/chain")
@PreAuthorize("@authService.isAdmin(principal)")
public class ChainController {
    private final ChainService chainService;

    @GetMapping("")
    public ResponseEntity<SuccessResponse<ChainListGetResponseDTO>> getChainList() {
        ChainListGetResponseDTO result = chainService.getChainList();

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("")
    public ResponseEntity<SuccessResponse<ChainAddResponseDTO>> addChain(
            @Valid @RequestBody ChainAddRequestDTO dto
    ) {
        ChainAddResponseDTO result = chainService.addChain(dto);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PutMapping("{chainId}")
    public ResponseEntity<SuccessResponse<ChainUpdateResponseDTO>> changeChain(
            @Valid @RequestBody ChainUpdateRequestDTO dto,
            @PathVariable Long chainId
    ) {
        ChainUpdateResponseDTO result = chainService.changeChain(dto, chainId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @DeleteMapping("{chainId}")
    public void removeChain(
            @PathVariable Long chainId
    ) {
        chainService.removeChain(chainId);
        return;
    }

    @GetMapping("{chainId}/contract/balance")
    public ResponseEntity<SuccessResponse<ContractBalanceGetResponseDTO>> getContractBalance(
            @PathVariable Long chainId
    ) {
        ContractBalanceGetResponseDTO result = chainService.getContractBalance(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("{chainId}/contract/balance")
    public ResponseEntity<SuccessResponse<Void>> addContractBalance(
            @Valid @RequestBody AddContractBalanceRequestDTO dto,
            @PathVariable Long chainId
    ) {
        if (dto.getBalance().compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("값이 0이 될 수 없습니다.");

        chainService.addContractBalance(dto, chainId);
        return ResponseEntity.ok(new SuccessResponse<>("" , null));
    }
}

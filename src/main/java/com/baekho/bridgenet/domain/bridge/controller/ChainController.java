package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.*;
import com.baekho.bridgenet.domain.bridge.service.ChainService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
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
@RequestMapping("api/bridge/chain")
public class ChainController {
    private final ChainService chainService;

    @GetMapping("")
    public ResponseEntity<SuccessResponse<ChainListGetResponseDTO>> getChainList() {
        ChainListGetResponseDTO result = chainService.getChainList();

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PostMapping("")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainAddResponseDTO>> addChain(
            @Valid @RequestBody ChainAddRequestDTO dto
    ) {
        ChainAddResponseDTO result = chainService.addChain(dto);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @PutMapping("{chainId}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<ChainUpdateResponseDTO>> changeChain(
            @Valid @RequestBody ChainUpdateRequestDTO dto,
            @PathVariable Long chainId
    ) {
        ChainUpdateResponseDTO result = chainService.changeChain(dto, chainId);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @DeleteMapping("{chainId}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public void removeChain(
            @PathVariable Long chainId
    ) {
        chainService.removeChain(chainId);
        return;
    }

    @GetMapping("/ranking")
    public ResponseEntity<SuccessResponse<List<GetChainRankingResponseDTO>>> getChainRanking(
            @RequestParam(name = "sort", defaultValue = "in") String sort
    ) {
        List<GetChainRankingResponseDTO> result = chainService.getChainRanking(sort);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

    @GetMapping("{chainId}/contract/balance")
    public ResponseEntity<SuccessResponse<ContractBalanceGetResponseDTO>> getContractBalance(
            @PathVariable Long chainId
    ) {
        ContractBalanceGetResponseDTO result = chainService.getContractBalance(chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }

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

    @PostMapping("{chainId}/contract/whitelist")
    public ResponseEntity<SuccessResponse<WhiteListResponseDTO>> setWhiteList(
            @Valid @RequestBody WhiteListRequestDTO dto
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        WhiteListResponseDTO result = chainService.setWhiteList(dto, user);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

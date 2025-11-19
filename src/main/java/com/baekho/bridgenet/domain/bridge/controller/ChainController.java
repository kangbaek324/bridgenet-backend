package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.bridge.dto.ChainAddRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.ChainAddResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.ChainListGetResponseDTO;
import com.baekho.bridgenet.domain.bridge.service.ChainService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("{id}")
    public void removeChain(
            @PathVariable Long id
    ) {
         chainService.removeChain(id);

        return;
    }
}

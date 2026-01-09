package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.service.ChainRpcService;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chains/{chainId}/rpcs")
public class RpcController {
    private final ChainRpcService chainRpcService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<RpcResponseDTO>>> getRpcs(
            @PathVariable Long chainId,
            @RequestParam(required = false) Protocol protocol
    ) {
        List<RpcResponseDTO> res = chainRpcService.getRpcs(chainId, protocol);

        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    @PostMapping
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<RpcAddResponseDTO>> addRpc(
            @RequestBody RpcAddRequestDTO dto,
            @PathVariable Long chainId
    ) {
        RpcAddResponseDTO res = chainRpcService.addRpc(dto, chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }
}

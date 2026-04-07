package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.service.ChainRpcService;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chains/{chainId}/rpcs")
public class ChainRpcController {
    private final ChainRpcService chainRpcService;

    @Operation(summary = "RPC 목록 조회", description = "체인에 연결된 RPC 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<SuccessResponse<List<RpcResponseDTO>>> getRpcs(
            @PathVariable Long chainId,
            @RequestParam(required = false) Protocol protocol
    ) {
        List<RpcResponseDTO> res = chainRpcService.getRpcs(chainId, protocol);

        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    @Operation(summary = "RPC 추가", description = "체인에 새로운 RPC를 추가합니다.")
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

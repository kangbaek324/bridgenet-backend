package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.service.RpcService;
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
    private final RpcService rpcService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<RpcResponseDTO>>> getRpcs(
            @PathVariable Long chainId,
            @RequestParam(required = false) Protocol protocol
    ) {
        List<RpcResponseDTO> res = rpcService.getRpcs(chainId, protocol);

        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<RpcAddResponseDTO>> addRpc(
            @RequestBody RpcAddRequestDTO dto,
            @PathVariable Long chainId
    ) {
        RpcAddResponseDTO res = rpcService.addRpc(dto, chainId);
        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<RpcUpdateResponseDTO>> updateRpc(
            @RequestBody RpcUpdateRequestDTO dto,
            @PathVariable Long id
    ) {
        RpcUpdateResponseDTO res = rpcService.updateRpc(dto, id);
        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteRpc(
            @PathVariable Long id
    ) {
        rpcService.deleteRpc(id);
        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }
}

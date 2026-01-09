package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.service.RpcService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rpcs")
public class ChainRpcController {
    private final RpcService rpcService;

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<RpcResponseDTO>> getRpc(
            @PathVariable Long id
    ) {
        RpcResponseDTO res = rpcService.getRpc(id);

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
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> deleteRpc(
            @PathVariable Long id
    ) {
        rpcService.deleteRpc(id);
        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }
}

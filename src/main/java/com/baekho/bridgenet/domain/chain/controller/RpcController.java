package com.baekho.bridgenet.domain.chain.controller;

import com.baekho.bridgenet.domain.chain.dto.response.ChainRpcGroupDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.service.RpcService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rpc")
public class RpcController {
    private final RpcService rpcService;

    @GetMapping
    public ResponseEntity<SuccessResponse<Map<Long, ChainRpcGroupDTO>>> getRpcs() {
        Map<Long, ChainRpcGroupDTO> res = rpcService.getRpcs();

        return ResponseEntity.ok(new SuccessResponse<>("", res));
    }
}

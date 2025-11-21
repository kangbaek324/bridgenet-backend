package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.service.BridgeService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/bridge")
public class BridgeController {
    private final BridgeService bridgeService;

    @PostMapping("request-option")
    @PreAuthorize("@authService.isAdmin(principal)")
    public ResponseEntity<SuccessResponse<Void>> setRequestOption(
            @Valid @RequestBody RequestOptionSetRequestDTO dto
    ) {
        bridgeService.setRequestOptionStatus(dto);

        return ResponseEntity.ok(new SuccessResponse<>("", null));
    }
}

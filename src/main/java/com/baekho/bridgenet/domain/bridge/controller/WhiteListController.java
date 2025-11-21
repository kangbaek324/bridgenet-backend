package com.baekho.bridgenet.domain.bridge.controller;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.WhiteListRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.WhiteListResponseDTO;
import com.baekho.bridgenet.domain.bridge.service.WhiteListService;
import com.baekho.bridgenet.global.common.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/bridge/whitelist")
public class WhiteListController {
    private final WhiteListService whiteListService;

    @PostMapping()
    public ResponseEntity<SuccessResponse<WhiteListResponseDTO>> setWhiteList(
            @Valid @RequestBody WhiteListRequestDTO dto
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        WhiteListResponseDTO result = whiteListService.setWhiteList(dto, user);

        return ResponseEntity.ok(new SuccessResponse<>("", result));
    }
}

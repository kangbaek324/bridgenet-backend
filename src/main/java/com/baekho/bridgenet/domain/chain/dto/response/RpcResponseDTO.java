package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RpcResponseDTO {
    private String serviceName;
    private String http;
    private String ws;
}

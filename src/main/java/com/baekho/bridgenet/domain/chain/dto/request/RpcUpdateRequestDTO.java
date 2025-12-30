package com.baekho.bridgenet.domain.chain.dto.request;

import lombok.Getter;

@Getter
public class RpcUpdateRequestDTO {
    private String serviceName;
    private String http;
    private String ws;
}

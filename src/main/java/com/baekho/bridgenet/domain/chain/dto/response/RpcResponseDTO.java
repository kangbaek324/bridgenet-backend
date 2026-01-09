package com.baekho.bridgenet.domain.chain.dto.response;

import com.baekho.bridgenet.global.common.enums.Protocol;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RpcResponseDTO {
    private Long id;
    private String serviceName;
    private String url;
    private Protocol protocol;
}

package com.baekho.bridgenet.domain.chain.dto.request;

import com.baekho.bridgenet.global.common.enums.Protocol;
import lombok.Getter;

@Getter
public class RpcUpdateRequestDTO {
    private String serviceName;
    private String url;
    private Protocol protocol;
}

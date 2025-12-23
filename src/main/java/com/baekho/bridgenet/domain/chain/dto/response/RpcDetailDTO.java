package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RpcDetailDTO {
    private Long chainId;
    private String chainName;
}

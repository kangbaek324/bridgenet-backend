package com.baekho.bridgenet.domain.bridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
public class BridgeHistoryChainInfo {
    private Long chainId;
    private String chainName;
    private String unit;
    private BigInteger value;
}
package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class ChainDetailBridgeHistoryDTO {
    private Long chainId;
    private String chainName;
    private BigInteger value;
    private String unit;
    private String transactionHash;
}

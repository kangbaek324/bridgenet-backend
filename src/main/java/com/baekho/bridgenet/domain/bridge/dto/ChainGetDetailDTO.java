package com.baekho.bridgenet.domain.bridge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChainGetDetailDTO {
    private Long chainId;
    private String chainName;
    private String smartContractAddress;
    private Long smartContractValue;
}

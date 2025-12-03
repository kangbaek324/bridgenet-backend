package com.baekho.bridgenet.domain.bridge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChainAddRequestDTO {
    @Schema(description = "체인 이름", example = "ethereum")
    @NotBlank
    private String chainName;

    @Schema(description = "체인 아이디", example = "1")
    @NotNull
    private Long chainId;

    @Schema(description = "스마트컨트랙트 주소", example = "0x...")
    @NotBlank
    private String smartContractAddress;

    @Schema(description = "현재 스마트컨트랙트의 양", example = "0.5ETH -> 0.5")
    @NotNull
    private Long smartContractValue;

    @Schema(description = "네이티브 토큰 단위", example = "ETH")
    @NotBlank
    private String unit;

    @Schema(description = "체인 Http RPC", example = "http://..")
    @NotBlank
    private String httpRpc;

    @Schema(description = "체인 Websocket RPC", example = "wss://..")
    @NotBlank
    private String wsRpc;
}

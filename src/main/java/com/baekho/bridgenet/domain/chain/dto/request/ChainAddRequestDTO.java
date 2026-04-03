package com.baekho.bridgenet.domain.chain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigInteger;

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

    @Schema(description = "네이티브 토큰 단위", example = "ETH")
    @NotBlank
    private String unit;

    @Schema(description = "컨트랙트가 생성된 블록 번호", example = "103221")
    @NotNull
    private BigInteger contractCreatedBlockNumber;

    @Schema(description = "가스당 최대 지불 한도", example = "10000000")
    @Min(0)
    @NotNull
    private BigInteger maxFeePerGas;

    @Schema(description = "가스당 최대 채굴자 팁 한도", example = "10000000")
    @Min(0)
    @NotNull
    private BigInteger maxPriorityFeePerGas;

    @Schema(description = "가스 리밋", example = "10000000")
    @Min(21000)
    @NotNull
    private BigInteger gasLimit;

    @NotNull
    @Min(1)
    private BigInteger requiredConfirmations;
}

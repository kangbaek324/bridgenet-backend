package com.baekho.bridgenet.domain.chain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChainListResponseDTO {
    private List<ChainDetailDTO> list;
}

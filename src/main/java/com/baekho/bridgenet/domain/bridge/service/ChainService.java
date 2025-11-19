package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.bridge.dto.*;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChainService {
    private final ChainsRepository chainsRepository;

    public ChainListGetResponseDTO getChainList() {
        List<Chains> chains = chainsRepository.findAll();
        List<ChainGetDetailDTO> chainGetDetailDTOS = new ArrayList<>();

        for(Chains chain : chains) {
            chainGetDetailDTOS.add(
                    new ChainGetDetailDTO(
                            chain.getId(),
                            chain.getChainName(),
                            chain.getSmartContractAddress(),
                            chain.getSmartContractValue()
                    )
            );
        }

        return new ChainListGetResponseDTO(chainGetDetailDTOS);
    }

    public ChainAddResponseDTO addChain(ChainAddRequestDTO dto) {
        Optional<Chains> existing = chainsRepository.findByChainId(dto.getChainId());
        if (existing.isPresent()) throw new ChainException(ChainErrorCode.ALREADY_EXIST_CHAIN_ID);

        Chains chain = Chains.builder()
                .chainId(dto.getChainId())
                .chainName(dto.getChainName())
                .smartContractAddress(dto.getSmartContractAddress())
                .smartContractValue(dto.getSmartContractValue())
                .httpRpc(dto.getHttpRpc())
                .wsRpc(dto.getWsRpc())
                .build();

        chainsRepository.save(chain);

        return new ChainAddResponseDTO(
                chain.getId(),
                chain.getChainId(),
                chain.getChainName(),
                chain.getSmartContractAddress(),
                chain.getSmartContractValue(),
                chain.getHttpRpc(),
                chain.getWsRpc()
        );
    }

    public ChainUpdateResponseDTO changeChain(ChainUpdateRequestDTO dto, Long id) {
        Chains chain = chainsRepository.findById(id)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        chain.setChainName(dto.getChainName());
        chain.setSmartContractAddress(dto.getSmartContractAddress());
        chain.setSmartContractValue(dto.getSmartContractValue());
        chain.setHttpRpc(dto.getHttpRpc());
        chain.setWsRpc(dto.getWsRpc());

        chainsRepository.save(chain);

        return new ChainUpdateResponseDTO(
                chain.getId(),
                chain.getChainId(),
                chain.getChainName(),
                chain.getSmartContractAddress(),
                chain.getSmartContractValue(),
                chain.getHttpRpc(),
                chain.getWsRpc()
        );
    }

    public void removeChain(Long id) {
        Chains chain = chainsRepository.findById(id)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        chainsRepository.delete(chain);
    }
}

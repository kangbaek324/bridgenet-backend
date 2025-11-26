package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.bridge.dto.WhiteListRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.WhiteListResponseDTO;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.contract.bridge.Bridge;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhiteListService {
    private final ChainsRepository chainsRepository;
    private final Map<Long, Bridge> bridgeMap;

    public WhiteListResponseDTO setWhiteList(WhiteListRequestDTO dto, Users user) throws Exception {
        Chains chain = chainsRepository.findByChainId(dto.getChainId())
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        TransactionReceipt receipt = bridgeMap.get(chain.getChainId()).setWhiteList(user.getAddress(), true).send();

        return new WhiteListResponseDTO(receipt.getTransactionHash());
    }
}

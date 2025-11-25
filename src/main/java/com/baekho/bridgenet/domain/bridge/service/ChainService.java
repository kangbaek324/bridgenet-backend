package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.bridge.dto.*;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.config.BlockchainConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ChainService {
    /**
     *  * @TODO
     *  * 스마트컨트랙트로 chainID 추가 요청 보내도록 수정해야됨
     */
    private final ChainsRepository chainsRepository;
    private final BlockchainConfig blockchainConfig;
    private final Credentials credentials;
    private final Map<Long, Bridge> bridgeMap;

    public ChainListGetResponseDTO getChainList() {
        List<Chains> chains = chainsRepository.findAll();
        List<ChainGetDetailDTO> chainGetDetailDTOS = new ArrayList<>();

        for(Chains chain : chains) {
            chainGetDetailDTOS.add(
                    new ChainGetDetailDTO(
                            chain.getChainId(),
                            chain.getChainName(),
                            chain.getSmartContractAddress(),
                            chain.getSmartContractValue()
                    )
            );
        }

        return new ChainListGetResponseDTO(chainGetDetailDTOS);
    }

    @Transactional
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
                .lastBlockNumber(BigInteger.valueOf(0))
                .build();

        chainsRepository.save(chain);

        Bridge bridge = blockchainConfig.createBridgeObject(chain);
        bridgeMap.put(chain.getChainId(), bridge);

        return new ChainAddResponseDTO(
                chain.getChainId(),
                chain.getChainName(),
                chain.getSmartContractAddress(),
                chain.getSmartContractValue(),
                chain.getHttpRpc(),
                chain.getWsRpc()
        );
    }

    @Transactional
    public ChainUpdateResponseDTO changeChain(ChainUpdateRequestDTO dto, Long chainId) {
        Chains chain = chainsRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        chain.setChainName(dto.getChainName());
        chain.setSmartContractAddress(dto.getSmartContractAddress());
        chain.setSmartContractValue(dto.getSmartContractValue());
        chain.setHttpRpc(dto.getHttpRpc());
        chain.setWsRpc(dto.getWsRpc());

        chainsRepository.save(chain);

        Bridge bridge = blockchainConfig.createBridgeObject(chain);
        bridgeMap.put(chain.getChainId(), bridge);

        return new ChainUpdateResponseDTO(
                chain.getChainId(),
                chain.getChainName(),
                chain.getSmartContractAddress(),
                chain.getSmartContractValue(),
                chain.getHttpRpc(),
                chain.getWsRpc()
        );
    }

    @Transactional
    public void removeChain(Long chainId) {
        Chains chain = chainsRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
        chainsRepository.delete(chain);

        blockchainConfig.createBridgeObject(chain);
        bridgeMap.remove(chain.getChainId());
    }

    public void addContractBalance(AddContractBalanceRequestDTO dto, Long chainId) {
//        Chains chain = chainsRepository.findByChainId(chainId)
//            .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
//
//        Bridge bridge = bridgeMap.get(chain.getChainId());
//        String sendAddress = bridge.getContractAddress();
//
//        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
//
//        )
    }
}

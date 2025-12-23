package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.request.AddContractBalanceRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.ChainAddRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.ChainUpdateRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.*;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.global.blockchain.BlockchainService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.BlockchainErrorCode;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.ChainException;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChainService {
    /**
     *  * @TODO
     *  * 스마트컨트랙트로 chainID 추가 요청 보내도록 수정해야됨
     */
    private final ChainRepository chainRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BlockchainService blockchainService;
    private final Map<Long, Bridge> bridgeMap;
    private final Map<Long, Web3j> httpWeb3jMap;
    private final Map<Long, Disposable> subMap;

    public ChainListResponseDTO getChainList() {
        List<Chain> chains = chainRepository.findAll();
        List<ChainDetailDTO> chainGetDetailDTOS = new ArrayList<>();

        for(Chain chain : chains) {
            chainGetDetailDTOS.add(
                    ChainDetailDTO.
                            builder()
                            .chainId(chain.getChainId())
                            .chainName(chain.getChainName())
                            .smartContractAddress(chain.getSmartContractAddress())
                            .smartContractValue(chain.getSmartContractValue())
                            .unit(chain.getUnit())
                            .build()
            );
        }

        return new ChainListResponseDTO(chainGetDetailDTOS);
    }

    @Transactional
    public ChainAddResponseDTO addChain(ChainAddRequestDTO dto) {
        Optional<Chain> existing = chainRepository.findByChainId(dto.getChainId());
        if (existing.isPresent()) throw new ChainException(ChainErrorCode.ALREADY_EXIST_CHAIN_ID);

        Chain chain = Chain.builder()
                .chainId(dto.getChainId())
                .chainName(dto.getChainName())
                .smartContractAddress(dto.getSmartContractAddress())
                .smartContractValue(dto.getSmartContractValue())
                .unit(dto.getUnit())
                .httpRpc(dto.getHttpRpc())
                .wsRpc(dto.getWsRpc())
                .lastBlockNumber(BigInteger.valueOf(0))
                .build();

        chainRepository.save(chain);

        Bridge bridge = blockchainService.createBridgeObject(chain);
        bridgeMap.put(chain.getChainId(), bridge);

        blockchainService.subscribeToContractEvents(bridge, chain, dto.getContractCreatedBlockNumber());

        return ChainAddResponseDTO
                .builder()
                .chainId(chain.getChainId())
                .chainName(chain.getChainName())
                .smartContractAddress(chain.getSmartContractAddress())
                .smartContractValue(chain.getSmartContractValue())
                .unit(chain.getUnit())
                .httpRpc(chain.getHttpRpc())
                .wsRpc(chain.getWsRpc())
                .build();
    }

    @Transactional
    public ChainUpdateResponseDTO changeChain(ChainUpdateRequestDTO dto, Long chainId) {
        Chain chain = chainRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        String beforeContractAddress = chain.getSmartContractAddress();
        String beforeHttpRpc = chain.getHttpRpc();

        chain.setChainName(dto.getChainName());
        chain.setSmartContractAddress(dto.getSmartContractAddress());
        chain.setSmartContractValue(dto.getSmartContractValue());
        chain.setUnit(dto.getUnit());
        chain.setHttpRpc(dto.getHttpRpc());
        chain.setWsRpc(dto.getWsRpc());

        chainRepository.save(chain);

        Bridge bridge = blockchainService.createBridgeObject(chain);
        bridgeMap.put(chain.getChainId(), bridge);

        // 스마트 컨트랙트가 또는 RPC 변경시
        if (
            !beforeContractAddress.equals(dto.getSmartContractAddress()) ||
            !beforeHttpRpc.equals(dto.getHttpRpc())
        ) {
            // 기존의 구독중이던 이벤트 끊기
            Disposable sub = subMap.get(chain.getChainId());
            sub.dispose();

            // 새 구독 등록
            blockchainService.subscribeToContractEvents(bridge, chain, chain.getLastBlockNumber());
        }

        return ChainUpdateResponseDTO.builder()
                .chainId(chain.getChainId())
                .chainName(chain.getChainName())
                .smartContractAddress(chain.getSmartContractAddress())
                .smartContractValue(chain.getSmartContractValue())
                .unit(chain.getUnit())
                .httpRpc(chain.getHttpRpc())
                .wsRpc(chain.getWsRpc())
                .build();
    }

    @Transactional
    public void removeChain(Long chainId) {
        Chain chain = chainRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
        chainRepository.delete(chain);

        blockchainService.createBridgeObject(chain);
        bridgeMap.remove(chain.getChainId());

        // 구독 취소
        Disposable sub = subMap.get(chain.getChainId());
        sub.dispose();
    }

    public ContractBalanceGetResponseDTO getContractBalance(Long chainId) {
        Chain chain = chainRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        Web3j web3j = httpWeb3jMap.get(chainId);

        EthGetBalance balance;

        try  {
            balance = web3j.ethGetBalance(
                    chain.getSmartContractAddress(),
                    DefaultBlockParameterName.LATEST
            ).send();
        } catch (Exception e) {
            log.error("Get Contract Balance Error: {}", e.getMessage(), e);
            throw new BlockchainException(BlockchainErrorCode.ERROR);
        }

        chain.setSmartContractValue(balance.getBalance());
        chainRepository.save(chain);

        return new ContractBalanceGetResponseDTO(
                balance.getBalance(),
                chain.getUnit()
        );
    }

    public List<ChainRankingResponseDTO> getChainRanking(String sort) {
        List<List<Object>> chainRankingDB = switch (sort) {
            case "in" -> exchangeRequestRepository.findTotalToValueByChain(RequestStatus.APPROVE);
            case "out" -> exchangeRequestRepository.findTotalFromValueByChain(RequestStatus.APPROVE);
            default -> throw new IllegalArgumentException("sort 는 in, out 만 허용합니다.");
        };

        List<ChainRankingResponseDTO> chainRanking = new ArrayList<>();
        int chainRankingIndex = 1;

        for (List<Object> chain : chainRankingDB) {
            chainRanking.add(
                ChainRankingResponseDTO
                    .builder()
                    .ranking(chainRankingIndex)
                    .chainId((Long) chain.get(0))
                    .chainName((String) chain.get(1))
                    .value((BigInteger) chain.get(2))
                    .unit((String) chain.get(3))
                    .build()
            );

            chainRankingIndex++;
        }

        return chainRanking;
    }

    public void addContractBalance(AddContractBalanceRequestDTO dto, Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
            .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        // @TODO 체인별 가스 지정 필요
        try {
            Bridge bridge = bridgeMap.get(chain.getChainId());
            bridge.addBalance(
                    Convert.toWei(dto.getBalance(), Convert.Unit.ETHER).toBigInteger()
            ).send();
        } catch (Exception e) {
            log.error("Add Contract Balance Error: {}", e.getMessage(), e);
            throw new BlockchainException(BlockchainErrorCode.ERROR);
        }
    }

    public WhiteListResponseDTO setWhiteList(Long chainId, Users user) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        TransactionReceipt receipt;

        try {
            receipt = bridgeMap.get(chain.getChainId()).setWhiteList(user.getAddress(), true).send();
        } catch (Exception e) {
            log.error("Set WhiteList Error: {}", e.getMessage(), e);
            throw new BlockchainException(BlockchainErrorCode.ERROR);
        }

        return new WhiteListResponseDTO(receipt.getTransactionHash());
    }
}

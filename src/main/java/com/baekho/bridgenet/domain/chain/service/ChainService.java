package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.chain.dto.request.AddContractBalanceRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.ChainAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.ChainUpdateRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.*;
import com.baekho.bridgenet.domain.chain.dto.response.*;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.domain.chain.repository.projection.ChainStatusProjection;
import com.baekho.bridgenet.global.blockchain.BlockchainEventService;
import com.baekho.bridgenet.global.blockchain.BlockchainRecoverService;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.blockchain.contract.SmartContractService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.BlockchainErrorCode;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.ChainException;
import io.reactivex.disposables.Disposable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    // @TODO 스마트컨트랙트로 chainID 추가 요청 보내도록 수정해야됨
    private final ChainRepository chainRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final RpcRepository rpcRepository;
    private final RpcState rpcState;

    private final BlockchainEventService blockchainEventService;
    private final BlockchainRecoverService blockchainRecoverService;
    private final RpcService rpcService;
    private final SmartContractService smartContractService;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;
    private final Map<Long, Disposable> subMap;

    public ChainListResponseDTO getChainList(Boolean status) {
        List<Chain> chains;

        if (status == null) {
            chains = chainRepository.findAll();
        } else {
            chains = chainRepository.findAllByStatus(status);
        }

        List<ChainDetailDTO> chainGetDetailDTOS = chains.stream()
                .map(chain -> ChainDetailDTO.builder()
                        .chainId(chain.getChainId())
                        .chainName(chain.getChainName())
                        .smartContractAddress(chain.getSmartContractAddress())
                        .smartContractValue(chain.getSmartContractValue())
                        .unit(chain.getUnit())
                        .build()
                )
                .toList();

        return new ChainListResponseDTO(chainGetDetailDTOS);
    }

    public AdminChainDetailDTO getChain(Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        return AdminChainDetailDTO.builder()
                .chainId(chain.getChainId())
                .chainName(chain.getChainName())
                .smartContractAddress(chain.getSmartContractAddress())
                .smartContractValue(chain.getSmartContractValue())
                .unit(chain.getUnit())
                .status(chain.isStatus())
                .maxFeePerGas(chain.getMaxFeePerGas())
                .maxPriorityFeePerGas(chain.getMaxPriorityFeePerGas())
                .gasLimit(chain.getGasLimit())
                .build();
    }

    public ChainAddResponseDTO addChain(ChainAddRequestDTO dto) {
        // 중복 검사
        boolean existingChainId = chainRepository.existsByChainId(dto.getChainId());
        boolean existingChainName = chainRepository.existsByChainName(dto.getChainName());
        if (existingChainId) throw new ChainException(ChainErrorCode.ALREADY_EXIST_CHAIN);
        else if (existingChainName) throw new ChainException(ChainErrorCode.ALREADY_EXIST_CHAIN_NAME);

        smartContractService.validateGasSetting(dto.getMaxFeePerGas(), dto.getMaxPriorityFeePerGas(), dto.getGasLimit());

        Chain chain = Chain.builder()
                .chainId(dto.getChainId())
                .chainName(dto.getChainName())
                .smartContractAddress(dto.getSmartContractAddress())
                .unit(dto.getUnit())
                .lastBlockNumber(dto.getContractCreatedBlockNumber())
                .maxFeePerGas(dto.getMaxFeePerGas())
                .maxPriorityFeePerGas(dto.getMaxPriorityFeePerGas())
                .gasLimit(dto.getGasLimit())
                .build();

        chainRepository.save(chain);

        return ChainAddResponseDTO
                .builder()
                .chainId(chain.getChainId())
                .chainName(chain.getChainName())
                .chainStatus(chain.isStatus())
                .smartContractAddress(chain.getSmartContractAddress())
                .smartContractValue(chain.getSmartContractValue())
                .unit(chain.getUnit())
                .maxFeePerGas(chain.getMaxFeePerGas())
                .maxPriorityFeePerGas(chain.getMaxPriorityFeePerGas())
                .gasLimit(chain.getGasLimit())
                .build();
    }

    public ChainUpdateResponseDTO updateChain(ChainUpdateRequestDTO dto, Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        // 이미 존재하는 이름인지 검사
        if (!chain.getChainName().equals(dto.getChainName())) {
            boolean existingChainName = chainRepository.existsByChainName(dto.getChainName());
            if (existingChainName) throw new ChainException(ChainErrorCode.ALREADY_EXIST_CHAIN_NAME);
        }

        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_MUST_DEACTIVATE);
        smartContractService.validateGasSetting(dto.getMaxFeePerGas(), dto.getMaxPriorityFeePerGas(), dto.getGasLimit());

        chain.setChainName(dto.getChainName());
        chain.setSmartContractAddress(dto.getSmartContractAddress());
        chain.setUnit(dto.getUnit());
        chain.setMaxFeePerGas(dto.getMaxFeePerGas());
        chain.setMaxPriorityFeePerGas(dto.getMaxPriorityFeePerGas());
        chain.setGasLimit(dto.getGasLimit());

        chainRepository.save(chain);

        return ChainUpdateResponseDTO
                .builder()
                .chainId(chain.getChainId())
                .chainName(chain.getChainName())
                .chainStatus(chain.isStatus())
                .smartContractAddress(chain.getSmartContractAddress())
                .unit(chain.getUnit())
                .maxFeePerGas(chain.getMaxFeePerGas())
                .maxPriorityFeePerGas(chain.getMaxPriorityFeePerGas())
                .gasLimit(chain.getGasLimit())
                .build();
    }

    public void removeChain(Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_MUST_DEACTIVATE);

        chainRepository.delete(chain);
    }

    public ChainStatusResponseDTO getChainStatus(Long chainId) {
        ChainStatusProjection projection  = chainRepository.findStatusByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        return new ChainStatusResponseDTO(projection.getStatus());
    }

    public void activateChain(Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_ALREADY_ACTIVATE);

        setupChainRuntime(chain);

        chain.setStatus(true);
        chainRepository.save(chain);
    }

    public void deActivateChain(Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
        if (!chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_ALREADY_DEACTIVATE);

        deleteChainRuntime(chain);

        chain.setStatus(false);
        chainRepository.save(chain);
    }

    public void setupChainRuntime(Chain chain) {
        Long chainId = chain.getChainId();

        // RPC 연결 HTTP
        List<Rpc> rpcs = rpcRepository.findAllByChainAndProtocol(chain, Protocol.HTTP);
        if (rpcs.isEmpty()) throw new ChainException(ChainErrorCode.RPC_NOT_CONNECTED);

        int rpcNumber = 0;
        for (Rpc rpc : rpcs) {
            rpcService.createHttpRpc(chain, rpc);
            rpcNumber++;
        }

        rpcState.setRpcNumber(chainId, rpcNumber);

        Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
        Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));
        BigInteger nowBlockNumber;

        try {
            nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();
        } catch (Exception e) {
            log.error("Get BlockNumber Error: {}", e.getMessage(), e);
            throw new BlockchainException(BlockchainErrorCode.ERROR);
        }

        try {
            blockchainRecoverService.recoverEvent(chain, nowBlockNumber);
        } catch (Exception e) {
            log.error("Recover Event Error: {}", e.getMessage(), e);
        }

        // Recover 작업이 빨리 끝나 미래 -> 과거 이벤트를 구독하는 오류를 해결하기 위해
        // nowBlockNumber 다음 블록이 나올때까지 대기 하는 코드
        BigInteger checkBlockNumber;
        do {
            try {
                checkBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Get BlockNumber Error: {}", e.getMessage(), e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        } while (nowBlockNumber.compareTo(checkBlockNumber) >= 0);

        blockchainEventService.subscribeToContractEvents(bridge, chain, nowBlockNumber);
    }

    public void deleteChainRuntime(Chain chain) {
        Long chainId = chain.getChainId();

        bridgeMap.remove(chainId);
        httpWeb3jMap.remove(chainId);

        Disposable event = subMap.get(chainId);
        if (event != null) event.dispose();
    }

    public ContractBalanceGetResponseDTO getContractBalance(Long chainId) {
        Chain chain = chainRepository.findById(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        Web3j web3j = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));

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

        try {
            Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
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
            receipt = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId)).setWhiteList(user.getAddress(), true).send();
        } catch (Exception e) {
            log.error("Set WhiteList Error: {}", e.getMessage(), e);
            throw new BlockchainException(BlockchainErrorCode.ERROR);
        }

        return new WhiteListResponseDTO(receipt.getTransactionHash());
    }
}

package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.response.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.ChainDetailApproveDTO;
import com.baekho.bridgenet.domain.chain.dto.response.ChainDetailBridgeHistoryDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.ExchangeApproveResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.bridge.sepcification.ExchangeRequestSpecification;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.BlockchainErrorCode;
import com.baekho.bridgenet.global.common.code.BridgeErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.BridgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BridgeService {
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    private final Map<Long, Bridge> bridgeMap;

    public void setRequestOptionStatus(RequestOptionSetRequestDTO dto, Users user) {
        ExchangeRequestOption option = exchangeRequestOptionRepository.findById(1L)
                .orElseGet(() -> {
                    return ExchangeRequestOption.builder()
                            .id(1L)
                            .autoApprove(dto.getStatus())
                            .updatedUser(user)
                            .build();
                });

        option.setAutoApprove(dto.getStatus());
        option.setUpdatedUser(user);

        exchangeRequestOptionRepository.save(option);
    }

    // @TODO getExchangeHistory와 함수 합치기
    public Page<BridgeHistoryResponseDTO> getExchangeAllHistory(
            String sortType,
            int size,
            int page,
            Long chainId,
            String direction,
            String status
    ) {
        Specification<ExchangeRequest> spec = Specification.unrestricted();
        Sort sort = switch (sortType) {
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "highFromValue" -> Sort.by(Sort.Direction.DESC, "fromValue");
            case "lowFromValue" -> Sort.by(Sort.Direction.ASC, "fromValue");
            case "highToValue" -> Sort.by(Sort.Direction.DESC, "toValue");
            case "lowToValue" -> Sort.by(Sort.Direction.ASC, "toValue");
            default -> throw new IllegalArgumentException("sort 는 latest, oldest, highFromValue, lowFromValue, highToValue, lowToValue 만 허용합니다");
        };
        Pageable pageable= PageRequest.of(page, size, sort);

        if (chainId == null && !direction.isEmpty()) {
            throw new IllegalArgumentException("chainId만 존재하거나 chainId, direction 두가지가 동시에 존재해야합니다");
        }

        if (chainId != null) {
            spec = spec.and(
                    switch (direction) {
                        case "in" -> ExchangeRequestSpecification.hasToChainId(chainId);
                        case "out" -> ExchangeRequestSpecification.hasFromChainId(chainId);
                        default -> ExchangeRequestSpecification.hasChainId(chainId);
                    }
            );
        }

        if (status != null) {
            RequestStatus statusType = switch (status) {
                case "approve" -> RequestStatus.APPROVE;
                case "pending" -> RequestStatus.PENDING;
                case "reject" -> RequestStatus.REJECT;
                default -> throw new IllegalArgumentException("status 는 approve, pending, reject 만 허용합니다.");
            };

            spec = spec.and(ExchangeRequestSpecification.hasStatus(statusType));
        }

        Page<ExchangeRequest> exchangeRequestPage = exchangeRequestRepository.findAll(spec, pageable);

        return exchangeRequestPage.map(exchangeRequest -> {
            Chain toChain = exchangeRequest.getToChain();
            Chain fromChain = exchangeRequest.getFromChain();

            ChainDetailBridgeHistoryDTO from = ChainDetailBridgeHistoryDTO
                    .builder()
                    .chainId(fromChain.getChainId())
                    .chainName(fromChain.getChainName())
                    .value(exchangeRequest.getFromValue())
                    .unit(fromChain.getUnit())
                    .transactionHash(exchangeRequest.getFromTransactionHash())
                    .build();

            ChainDetailBridgeHistoryDTO to = ChainDetailBridgeHistoryDTO
                    .builder()
                    .chainId(toChain.getChainId())
                    .chainName(toChain.getChainName())
                    .value(exchangeRequest.getToValue())
                    .unit(toChain.getUnit())
                    .transactionHash(exchangeRequest.getToTransactionHash())
                    .build();

            return BridgeHistoryResponseDTO
                    .builder()
                    .id(exchangeRequest.getId())
                    .from(from)
                    .to(to)
                    .status(exchangeRequest.getApproveStatus())
                    .exchangedAt(exchangeRequest.getApprovedAt())
                    .createdAt(exchangeRequest.getCreatedAt())
                    .build();
        });
    }

    public List<BridgeHistoryResponseDTO> getExchangeHistory(Users user, String status) {
        List<ExchangeRequest> DB;

        if (status == null) {
            DB = exchangeRequestRepository.findAllByUser(user);
        }
        else {
            RequestStatus statusType = null;
            status = status.toLowerCase();

            statusType = switch (status) {
                case "approve" -> RequestStatus.APPROVE;
                case "reject" -> RequestStatus.REJECT;
                case "pending" -> RequestStatus.PENDING;
                default -> throw new IllegalArgumentException();
            };

            DB = exchangeRequestRepository.findAllByApproveStatus(statusType);
        }

        List<BridgeHistoryResponseDTO> result = new ArrayList<>();

        for (ExchangeRequest exchangeRequest : DB) {
            Chain toChain = exchangeRequest.getToChain();
            Chain fromChain = exchangeRequest.getFromChain();

            ChainDetailBridgeHistoryDTO from = ChainDetailBridgeHistoryDTO
                    .builder()
                    .chainId(fromChain.getChainId())
                    .chainName(fromChain.getChainName())
                    .value(exchangeRequest.getFromValue())
                    .unit(fromChain.getUnit())
                    .transactionHash(exchangeRequest.getFromTransactionHash())
                    .build();

            ChainDetailBridgeHistoryDTO to = ChainDetailBridgeHistoryDTO
                    .builder()
                    .chainId(toChain.getChainId())
                    .chainName(toChain.getChainName())
                    .value(exchangeRequest.getToValue())
                    .unit(toChain.getUnit())
                    .transactionHash(exchangeRequest.getToTransactionHash())
                    .build();

            result.add(
                BridgeHistoryResponseDTO
                        .builder()
                        .id(exchangeRequest.getId())
                        .from(from)
                        .to(to)
                        .status(exchangeRequest.getApproveStatus())
                        .exchangedAt(exchangeRequest.getApprovedAt())
                        .createdAt(exchangeRequest.getCreatedAt())
                        .build()
            );
        }

        return result;
    }

    @Transactional
    public ExchangeApproveResponseDTO setRequest(ExchangeApproveRequestDTO dto, Long id, Users user) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(()-> new BridgeException(BridgeErrorCode.REQUEST_NOT_FOUND));

        if (request.getApproveStatus() != RequestStatus.PENDING) throw new BridgeException(BridgeErrorCode.REQUEST_ALREADY_PROCESSED);

        request.setApproveStatus(dto.getApproveStatus() ? RequestStatus.APPROVE : RequestStatus.REJECT);
        request.setApproveUser(user);
        request.setApprovedAt(LocalDateTime.now());

        String transactionHash = "";
        TransactionReceipt receipt;
        if (dto.getApproveStatus()) {
            try {
                 Bridge bridge = bridgeMap.get(request.getToChain().getChainId());
                 receipt = bridge.triggerPayout(request.getUser().getAddress(), request.getFromValue()).send();
            } catch (Exception e) {
                log.error("Trigger Payout Error: {}", e.getMessage(), e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        }
        else {
            try {
                Bridge bridge = bridgeMap.get(request.getFromChain().getChainId());
                receipt = bridge.cancelRequest(request.getIdInSmartContract()).send();
            } catch (Exception e) {
                log.error("Cancel Request Error: {}", e.getMessage(), e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        }

        transactionHash = receipt.getTransactionHash();
        request.setToTransactionHash(transactionHash);

        // Response
        Chain toChain = request.getToChain();
        Chain fromChain = request.getFromChain();

        ChainDetailApproveDTO from = ChainDetailApproveDTO
                .builder()
                .chainId(fromChain.getChainId())
                .chainName(fromChain.getChainName())
                .value(request.getFromValue())
                .unit(fromChain.getUnit())
                .transactionHash(request.getFromTransactionHash())
                .build();

        ChainDetailApproveDTO to = ChainDetailApproveDTO
                .builder()
                .chainId(toChain.getChainId())
                .chainName(toChain.getChainName())
                .value(request.getToValue())
                .unit(toChain.getUnit())
                .transactionHash(request.getToTransactionHash())
                .build();

        return ExchangeApproveResponseDTO
                .builder()
                .id(request.getId())
                .from(from)
                .to(to)
                .approveStatus(request.getApproveStatus())
                .approvedAt(request.getApprovedAt())
                .transactionHash(transactionHash)
                .build();
    }
}

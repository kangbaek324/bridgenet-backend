package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.bridge.sepcification.ExchangeRequestSpecification;
import com.baekho.bridgenet.global.common.code.BlockchainErrorCode;
import com.baekho.bridgenet.global.common.code.BridgeErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.BridgeException;
import com.baekho.bridgenet.global.contract.bridge.Bridge;
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

        if (chainId == null && direction != null) {
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
            return new BridgeHistoryResponseDTO(
                    exchangeRequest.getId(),
                    exchangeRequest.getFromChain().getChainId(),
                    exchangeRequest.getFromValue(),
                    exchangeRequest.getToChain().getChainId(),
                    exchangeRequest.getToValue(),
                    exchangeRequest.getApproveStatus(),
                    exchangeRequest.getTransactionHash(),
                    exchangeRequest.getApprovedAt(),
                    exchangeRequest.getCreatedAt()
            );
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
            result.add(
                new BridgeHistoryResponseDTO(
                        exchangeRequest.getId(),
                        exchangeRequest.getFromChain().getChainId(),
                        exchangeRequest.getFromValue(),
                        exchangeRequest.getToChain().getChainId(),
                        exchangeRequest.getToValue(),
                        exchangeRequest.getApproveStatus(),
                        exchangeRequest.getTransactionHash(),
                        exchangeRequest.getApprovedAt(),
                        exchangeRequest.getCreatedAt()
                )
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
        TransactionReceipt recepit;
        if (dto.getApproveStatus()) {
            try {
                 Bridge bridge = bridgeMap.get(request.getToChain().getChainId());
                 recepit = bridge.triggerPayout(request.getUser().getAddress(), request.getFromValue()).send();
            } catch (Exception e) {
                log.error("Trigger Payout Error: {}", e.getMessage(), e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        }
        else {
            try {
                Bridge bridge = bridgeMap.get(request.getFromChain().getChainId());
                recepit = bridge.cancelRequest(request.getIdInSmartContract()).send();
            } catch (Exception e) {
                log.error("Cancel Request Error: {}", e.getMessage(), e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        }

        transactionHash = recepit.getTransactionHash();
        request.setTransactionHash(transactionHash);

        return new ExchangeApproveResponseDTO(
                request.getId(),
                request.getFromChain().getChainId(),
                request.getFromValue(),
                request.getToChain().getChainId(),
                request.getToValue(),
                request.getApproveStatus(),
                transactionHash,
                request.getApprovedAt()
        );
    }
}

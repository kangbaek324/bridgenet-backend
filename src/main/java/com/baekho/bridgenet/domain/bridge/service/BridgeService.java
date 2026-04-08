package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.bridge.dto.request.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.response.*;
import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.BridgeTransactionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.bridge.sepcification.ExchangeRequestSpecification;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.BridgeErrorCode;
import com.baekho.bridgenet.global.common.enums.ApproveStatus;
import com.baekho.bridgenet.global.common.enums.TransactionType;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BridgeService {
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BridgeTransactionRepository bridgeTransactionRepository;
    private final RpcState rpcState;

    private final Map<Long, List<Bridge>> bridgeMap;

    // TODO: 취소 요청 필요
    // TODO: REJECT시 환불 요청 실행시켜야됨
    public void setRequestOptionStatus(RequestOptionSetRequestDTO dto, User user) {
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

    public RequestHistoryResponse getRequestHistory(Long id) {
        // TODO: DB 쿼리 최적화 필요
        ExchangeRequest exReq = exchangeRequestRepository.findById(id)
                .orElseThrow(() -> new BridgeException(BridgeErrorCode.REQUEST_NOT_FOUND));

        List<BridgeTransaction> fromTx = bridgeTransactionRepository.findByExchangeRequestAndType(exReq, TransactionType.FROM);
        List<BridgeTransaction> toTx = bridgeTransactionRepository.findByExchangeRequestAndType(exReq, TransactionType.TO);

        List<TransactionInfo> fromTxInfo = fromTx.stream()
                .map(tx -> new TransactionInfo(tx.getTransactionHash(), tx.getProcessedBlock(), tx.getStatus()))
                .toList();

        List<TransactionInfo> toTxInfo = toTx.stream()
                .map(tx -> new TransactionInfo(tx.getTransactionHash(), tx.getProcessedBlock(), tx.getStatus()))
                .toList();

        Chain fromChain = exReq.getFromChain();
        Chain toChain = exReq.getToChain();

        return RequestHistoryResponse.builder()
                .id(exReq.getId())
                .from(
                    new RequestHistoryResponse.BridgeInfo(
                        new BridgeHistoryChainInfo(
                                fromChain.getChainId(),
                                fromChain.getChainName(),
                                fromChain.getUnit(),
                                exReq.getFromValue()
                        ),
                        fromTxInfo
                    )
                )
                .to(
                    new RequestHistoryResponse.BridgeInfo(
                        new BridgeHistoryChainInfo(
                                toChain.getChainId(),
                                toChain.getChainName(),
                                toChain.getUnit(),
                                exReq.getToValue()
                        ),
                        toTxInfo
                    )
                )
                .approveStatus(exReq.getApproveStatus())
                .bridgeStatus(exReq.getBridgeStatus())
                .createdAt(exReq.getCreatedAt())
                .build();
    }

    public Page<BridgeHistoryResponseDTO> getExchangeHistory(
            String sortType,
            int size,
            int page,
            Long chainId,
            String direction,
            String status,
            User user
    ) {
        if (direction == null) direction = "";

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
            ApproveStatus statusType = switch (status) {
                case "approve" -> ApproveStatus.APPROVE;
                case "pending" -> ApproveStatus.PENDING;
                case "reject" -> ApproveStatus.REJECT;
                default -> throw new IllegalArgumentException("status 는 approve, pending, reject 만 허용합니다.");
            };

            spec = spec.and(ExchangeRequestSpecification.hasStatus(statusType));
        }

        spec = spec.and(ExchangeRequestSpecification.withUser(user)); // 유저 조건 추가

        Page<ExchangeRequest> exchangeRequestPage = exchangeRequestRepository.findAll(spec, pageable);

        return exchangeRequestPage.map(exReq -> {
            return BridgeHistoryResponseDTO.builder()
                    .id(exReq.getId())
                    .from(
                            new BridgeHistoryChainInfo(
                                    exReq.getFromChain().getChainId(),
                                    exReq.getFromChain().getChainName(),
                                    exReq.getFromChain().getUnit(),
                                    exReq.getFromValue()
                            )
                    )
                    .to(
                            new BridgeHistoryChainInfo(
                                    exReq.getToChain().getChainId(),
                                    exReq.getToChain().getChainName(),
                                    exReq.getToChain().getUnit(),
                                    exReq.getToValue()
                            )
                    )
                    .approveStatus(exReq.getApproveStatus())
                    .bridgeStatus(exReq.getBridgeStatus())
                    .createdAt(exReq.getCreatedAt())
                    .build();
        });
    }

    @Transactional
    public void setRequest(ExchangeApproveRequestDTO dto, Long id, User user) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(()-> new BridgeException(BridgeErrorCode.REQUEST_NOT_FOUND));

        if (request.getApproveStatus() != ApproveStatus.PENDING) throw new BridgeException(BridgeErrorCode.REQUEST_ALREADY_PROCESSED);

        request.setApproveStatus(dto.getApproveStatus() ? ApproveStatus.APPROVE : ApproveStatus.REJECT);
        request.setApproveUser(user);
        request.setApprovedAt(LocalDateTime.now());

        exchangeRequestRepository.save(request);
    }
}

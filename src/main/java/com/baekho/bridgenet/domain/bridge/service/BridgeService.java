package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.request.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.request.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.BridgeErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BridgeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RpcState rpcState;

    private final Map<Long, List<Bridge>> bridgeMap;

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

//    public Page<BridgeHistoryResponseDTO> getExchangeHistory(
//            String sortType,
//            int size,
//            int page,
//            Long chainId,
//            String direction,
//            String status,
//            Users user
//    ) {
//        if (direction == null) direction = "";
//
//        Specification<ExchangeRequest> spec = Specification.unrestricted();
//        Sort sort = switch (sortType) {
//            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
//            case "oldest" -> Sort.by(Sort.Direction.ASC, "createdAt");
//            case "highFromValue" -> Sort.by(Sort.Direction.DESC, "fromValue");
//            case "lowFromValue" -> Sort.by(Sort.Direction.ASC, "fromValue");
//            case "highToValue" -> Sort.by(Sort.Direction.DESC, "toValue");
//            case "lowToValue" -> Sort.by(Sort.Direction.ASC, "toValue");
//            default -> throw new IllegalArgumentException("sort 는 latest, oldest, highFromValue, lowFromValue, highToValue, lowToValue 만 허용합니다");
//        };
//        Pageable pageable= PageRequest.of(page, size, sort);
//
//        if (chainId == null && !direction.isEmpty()) {
//            throw new IllegalArgumentException("chainId만 존재하거나 chainId, direction 두가지가 동시에 존재해야합니다");
//        }
//
//        if (chainId != null) {
//            spec = spec.and(
//                    switch (direction) {
//                        case "in" -> ExchangeRequestSpecification.hasToChainId(chainId);
//                        case "out" -> ExchangeRequestSpecification.hasFromChainId(chainId);
//                        default -> ExchangeRequestSpecification.hasChainId(chainId);
//                    }
//            );
//        }
//
//        if (status != null) {
//            RequestStatus statusType = switch (status) {
//                case "approve" -> RequestStatus.APPROVE;
//                case "pending" -> RequestStatus.PENDING;
//                case "reject" -> RequestStatus.REJECT;
//                default -> throw new IllegalArgumentException("status 는 approve, pending, reject 만 허용합니다.");
//            };
//
//            spec = spec.and(ExchangeRequestSpecification.hasStatus(statusType));
//        }
//
//        spec = spec.and(ExchangeRequestSpecification.withUser(user)); // 유저 조건 추가
//
//        Page<ExchangeRequest> exchangeRequestPage = exchangeRequestRepository.findAll(spec, pageable);
//
//        return exchangeRequestPage.map(exchangeRequest -> {
//            Chain toChain = exchangeRequest.getToChain();
//            Chain fromChain = exchangeRequest.getFromChain();
//
//            ChainDetailBridgeHistoryDTO from = ChainDetailBridgeHistoryDTO
//                    .builder()
//                    .chainId(fromChain.getChainId())
//                    .chainName(fromChain.getChainName())
//                    .value(exchangeRequest.getFromValue())
//                    .unit(fromChain.getUnit())
//                    .transactionHash(exchangeRequest.getFromTransactionHash())
//                    .build();
//
//            ChainDetailBridgeHistoryDTO to = ChainDetailBridgeHistoryDTO
//                    .builder()
//                    .chainId(toChain.getChainId())
//                    .chainName(toChain.getChainName())
//                    .value(exchangeRequest.getToValue())
//                    .unit(toChain.getUnit())
//                    .transactionHash(exchangeRequest.getToTransactionHash())
//                    .build();
//
//            return BridgeHistoryResponseDTO
//                    .builder()
//                    .id(exchangeRequest.getId())
//                    .from(from)
//                    .to(to)
//                    .status(exchangeRequest.getApproveStatus())
//                    .exchangedAt(exchangeRequest.getApprovedAt())
//                    .createdAt(exchangeRequest.getCreatedAt())
//                    .build();
//        });
//    }

    @Transactional
    public void setRequest(ExchangeApproveRequestDTO dto, Long id, Users user) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(()-> new BridgeException(BridgeErrorCode.REQUEST_NOT_FOUND));

        if (request.getApproveStatus() != RequestStatus.PENDING) throw new BridgeException(BridgeErrorCode.REQUEST_ALREADY_PROCESSED);

        request.setApproveStatus(dto.getApproveStatus() ? RequestStatus.APPROVE : RequestStatus.REJECT);
        request.setApproveUser(user);
        request.setApprovedAt(LocalDateTime.now());

        exchangeRequestRepository.save(request);
    }
}

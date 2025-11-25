package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.ExchangeApproveResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import com.baekho.bridgenet.global.common.code.BlockchainErrorCode;
import com.baekho.bridgenet.global.common.code.BridgeErrorCode;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.RequestStatus;
import com.baekho.bridgenet.global.common.exception.BlockchainException;
import com.baekho.bridgenet.global.common.exception.BridgeException;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BridgeService {
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final ChainsRepository chainsRepository;

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

//    public List<BridgeHistoryResponseDTO> getExchangeHistory(Users user) {
//        List<ExchangeHistory> DB = exchangeHistoryRepository.findAllByUser(user);
//        List<BridgeHistoryResponseDTO> result = new ArrayList<>();
//
//        for (ExchangeHistory exchangeHistory : DB) {
//            result.add(
//                    new BridgeHistoryResponseDTO(
//                            exchangeHistory.getId(),
//                            exchangeHistory.getFromChain().getChainId(),
//                            exchangeHistory.getFromValue().toString(),
//                            exchangeHistory.getToChain().getChainId(),
//                            exchangeHistory.getToValue().toString(),
//                            exchangeHistory.getExchangedAt()
//                    )
//            );
//        }
//
//        return result;
//    }

    @Transactional
    public ExchangeApproveResponseDTO setRequest(ExchangeApproveRequestDTO dto, Long id, Users user) {
        ExchangeRequest request = exchangeRequestRepository.findById(id)
                .orElseThrow(()-> new BridgeException(BridgeErrorCode.REQUEST_NOT_FOUND));

        if (request.getApproveStatus() != RequestStatus.PENDING) throw new BridgeException(BridgeErrorCode.REQUEST_ALREADY_PROCESSED);

        request.setApproveStatus(dto.getApproveStatus() ? RequestStatus.APPROVE : RequestStatus.REJECT);
        request.setApproveUser(user);
        request.setApprovedAt(LocalDateTime.now());

        String transactionHash = "";
        if (dto.getApproveStatus()) {
            Bridge bridge = bridgeMap.get(request.getToChain().getChainId());
            try {
                 TransactionReceipt recepit = bridge.triggerPayout(request.getUser().getAddress(), request.getFromValue()).send();
                 transactionHash = recepit.getTransactionHash();
            } catch (Exception e) {
                log.error("Trigger Payout Error: {}", e);
                throw new BlockchainException(BlockchainErrorCode.ERROR);
            }
        }

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

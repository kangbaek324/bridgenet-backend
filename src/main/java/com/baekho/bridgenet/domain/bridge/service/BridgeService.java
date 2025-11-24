package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.auth.repository.UserRepository;
import com.baekho.bridgenet.domain.bridge.dto.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.entity.Chains;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeHistory;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ChainsRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeHistoryRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BridgeService {
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final ChainsRepository chainsRepository;

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

    public List<BridgeHistoryResponseDTO> getExchangeHistory(Users user) {
        List<ExchangeHistory> DB = exchangeHistoryRepository.findAllByUser(user);
        List<BridgeHistoryResponseDTO> result = new ArrayList<>();

        for (ExchangeHistory exchangeHistory : DB) {
            result.add(
                    new BridgeHistoryResponseDTO(
                            exchangeHistory.getId(),
                            exchangeHistory.getFromChain().getChainId(),
                            exchangeHistory.getFromValue().toString(),
                            exchangeHistory.getToChain().getChainId(),
                            exchangeHistory.getToValue().toString(),
                            exchangeHistory.getExchangedAt()
                    )
            );
        }

        return result;
    }

    @Transactional
    public void saveRequest(Bridge.RequestedEventResponse res) {
        Bridge.RequestInfo request = res.request;

        Optional<Users> userOpt = userRepository.findByAddress(res.requestAddress);
        Optional<Chains> chainOpt = chainsRepository.findByChainId(request.fromChainId.longValue());

        if (userOpt.isPresent() && chainOpt.isPresent()) {
            Users user = userOpt.get();
            Chains chain = chainOpt.get();

            Chains toChain = chainsRepository.findByChainId(request.toChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));
            Chains fromChain = chainsRepository.findByChainId(request.fromChainId.longValue())
                    .orElseThrow(()-> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

            exchangeRequestRepository.save(
                    ExchangeRequest.builder()
                    .toChain(toChain)
                    .toValue(request.toValue)
                    .fromChain(fromChain)
                    .fromValue(request.fromValue)
                    .user(user)
                    .build()
            );

            chain.setLastBlockNumber(res.log.getBlockNumber());

            log.info("Save RequestEvent Success: Request ID: {}", request.id);
        }
        else if (chainOpt.isEmpty()) {
            log.warn("알 수 없는 체인: {}", request.fromChainId.longValue());
        }
        else {
            log.warn("알 수 없는 주소: {}", res.requestAddress);
        }
    }
}

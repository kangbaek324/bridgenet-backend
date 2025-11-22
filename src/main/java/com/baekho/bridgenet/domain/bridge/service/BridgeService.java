package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.domain.bridge.dto.BridgeHistoryResponseDTO;
import com.baekho.bridgenet.domain.bridge.dto.RequestOptionSetRequestDTO;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeHistory;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequestOption;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeHistoryRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BridgeService {
    private final ExchangeRequestOptionRepository exchangeRequestOptionRepository;
    private final ExchangeHistoryRepository exchangeHistoryRepository;

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
}

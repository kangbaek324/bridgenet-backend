package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.service.ChainService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {
    private final ChainRepository chainRepository;
    private final ChainService chainService;

    @PostConstruct
    public void init() {
        List<Chain> chains = chainRepository.findAllByStatus(true);

        for (Chain chain : chains) {
            chainService.setupChainRuntime(chain);
        }
    }
}

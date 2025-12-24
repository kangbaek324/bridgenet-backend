package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.dto.ChainCountDTO;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RpcState {
    private final RpcRepository rpcRepository;

    // RPC 갯수
    private final Map<Long, Long> rpcNumber = new HashMap<>();;
    private final Map<Long, Integer> rpcCount = new HashMap<>();;

    @PostConstruct
    public void init() {
        List<ChainCountDTO> counts = rpcRepository.countByChainId();

        counts.forEach(count -> {
            rpcNumber.put(count.chainId(), count.count());

            rpcCount.put(count.chainId(), 0);
        });
    }

    public int rpcCount(Long chainId) {
        long chainRpcNumber = rpcNumber.get(chainId);
        int chainRpcCount = rpcCount.get(chainId);

        if (chainRpcNumber == 0) throw new IllegalStateException("RPC가 존재하지 않습니다.");

        if (chainRpcCount > chainRpcNumber - 1) {
            rpcCount.put(chainId, 1);
            return 0;
        };

        rpcCount.put(chainId, chainRpcCount + 1);
        return chainRpcCount;
    }
}

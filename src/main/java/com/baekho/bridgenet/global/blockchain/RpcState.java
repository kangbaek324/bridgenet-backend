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

    // @TODO RPC 추가/삭제시 카운드가 늘어나도록 변경해야됨
    public int rpcCount(Long chainId) {
        Long chainRpcNumber = rpcNumber.get(chainId);
        if (chainRpcNumber == null) throw new IllegalStateException("RPC가 존재하지 않습니다.");

        int chainRpcCount = rpcCount.get(chainId);

        if (chainRpcCount > chainRpcNumber - 1) {
            rpcCount.put(chainId, 1);
            return 0;
        };

        rpcCount.put(chainId, chainRpcCount + 1);
        return chainRpcCount;
    }
}

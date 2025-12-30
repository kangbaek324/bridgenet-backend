package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.dto.ChainCountDTO;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.common.enums.RpcUpdateDirection;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class RpcState {
    private final RpcRepository rpcRepository;

    private final Map<Long, Long> rpcNumber = new ConcurrentHashMap<>(); // RPC 갯수 (ChainID - Number)
    private final Map<Long, AtomicLong> rpcCount = new ConcurrentHashMap<>(); // 현재 사용해야될 번째의 RPC (ChainId - Number)

    @PostConstruct
    public void init() {
        List<ChainCountDTO> counts = rpcRepository.countByChainId();

        counts.forEach(count -> {
            rpcNumber.put(count.chainId(), count.count());
            rpcCount.put(count.chainId(), new AtomicLong(0));
        });
    }

    public int rpcCount(Long chainId) {
        Long size = rpcNumber.get(chainId);
        if (size == null || size <= 0) {
            throw new IllegalStateException("사용 가능한 RPC가 없습니다.");
        }

        // 새로운 체인을 추가한뒤 새로운 RPC를 추가할때
        AtomicLong cursor = rpcCount.computeIfAbsent(chainId, k -> new AtomicLong(0));

        long index = cursor.getAndIncrement();
        return (int) (index % size);
    }

    public void updateRpcNumber(Long chainId, int number, RpcUpdateDirection direction) {
        if (number < 0) number = 0; // 음수 방지

        Long chainRpcNumber = rpcNumber.get(chainId);
        if (chainRpcNumber == null) chainRpcNumber = 0L; // 새로운 체인을 추가한뒤 새로운 RPC를 추가할때

        switch (direction) {
            case INCREASE -> rpcNumber.put(chainId, chainRpcNumber + number);
            case DECREASE -> {
                long newChainRpcNumber = chainRpcNumber - number;
                if (newChainRpcNumber < 0) throw new IllegalStateException("RPC 갯수는 음수가 될 수 없습니다.");
                rpcNumber.put(chainId, newChainRpcNumber);

                AtomicLong index = rpcCount.computeIfAbsent(chainId, k -> new AtomicLong(0));
                index.set(0);
            }
            default -> throw new IllegalStateException("Unknown direction: " + direction);
        }
    }
}

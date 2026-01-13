package com.baekho.bridgenet.global.blockchain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class RpcState {
    private final Map<Long, Integer> rpcNumberMap = new ConcurrentHashMap<>(); // RPC 갯수 (ChainID - Number)
    private final Map<Long, AtomicLong> rpcCountMap = new ConcurrentHashMap<>(); // 현재 사용해야될 번째의 RPC (ChainId - Number)

    public int rpcCount(Long chainId) {
        Integer size = rpcNumberMap.get(chainId);
        if (size == null || size <= 0) {
            throw new IllegalStateException("사용 가능한 RPC가 없습니다.");
        }

        AtomicLong cursor = rpcCountMap.computeIfAbsent(chainId, k -> new AtomicLong(0));

        long index = cursor.getAndIncrement();
        return (int) (index % size);
    }

    public void setRpcNumber(Long chainId, int rpcNumber) {
        rpcNumberMap.put(chainId, rpcNumber);
    }
}

package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.domain.chain.service.RpcService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.enums.Protocol;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {
    private final ChainRepository chainRepository;
    private final RpcRepository rpcRepository;
    private final RpcState rpcState;

    private final RpcService rpcService;
    private final BlockchainEventService blockchainEventService;
    private final BlockchainRecoverService blockchainRecoverService;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;

    @PostConstruct
    public void init() throws IOException {
        List<Chain> chains = chainRepository.findAllByStatus(true);

        // Bridge 컨트랙트 인스턴스 Map 에 추가
        for (Chain chain : chains) {
            List<Rpc> rpcs = rpcRepository.findAllByChainAndProtocol(chain, Protocol.HTTP);

            // RPC 등록
            for (Rpc rpc : rpcs) {
                rpcService.createHttpRpc(chain, rpc);
            }
        }

        for (Chain chain : chains) {
            // 누락 이벤트 복구 및 이벤트 리스너 등록
            Long chainId = chain.getChainId();

            Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
            Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));
            BigInteger nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();

            try {
                blockchainRecoverService.recoverEvent(chain, nowBlockNumber);
            } catch (Exception e) {
                log.error("Recover Event Error: {}", e.getMessage(), e);
            }

            blockchainEventService.subscribeToContractEvents(bridge, chain, nowBlockNumber);
        }
    }
}

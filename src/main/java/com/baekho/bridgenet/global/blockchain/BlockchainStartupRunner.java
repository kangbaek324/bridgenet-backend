package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.domain.chain.service.ChainService;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.ChainStatus;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@RequiredArgsConstructor
public class BlockchainStartupRunner implements ApplicationRunner {

    private final ChainRepository chainRepository;
    private final RpcRepository rpcRepository;
    private final ChainService chainService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Chain> chains = chainRepository.findAllByStatus(ChainStatus.ACTIVATE);

        for (Chain chain : chains) {
            List<Rpc> rpcs = rpcRepository.findAllByChainAndProtocol(chain, Protocol.HTTP);
            if (rpcs.isEmpty()) {
                throw new ChainException(ChainErrorCode.RPC_NOT_CONNECTED);
            }

            chainService.setupChainRuntime(chain, rpcs).get();
        }
    }
}


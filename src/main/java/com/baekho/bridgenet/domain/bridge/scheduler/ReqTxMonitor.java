package com.baekho.bridgenet.domain.bridge.scheduler;

import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.repository.BridgeTransactionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReqTxMonitor {
    private final ChainRepository chainRepository;
    private final BridgeTransactionRepository bridgeTransactionRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    private final Map<Long, List<Web3j>> httpWeb3jMap;
    private final RpcState rpcState;

    @Scheduled(fixedDelay = 1000 * 60) // 1분
    public void reqTxMonitor() {
        if (httpWeb3jMap.isEmpty()) return;

        List<Chain> chains = chainRepository.findAll();

        for (Chain chain : chains) {
            List<Web3j> web3jList = httpWeb3jMap.get(chain.getChainId());
            if (web3jList == null || web3jList.isEmpty()) continue;

            Web3j httpWeb3 = web3jList.get(rpcState.rpcCount(chain.getChainId()));

            BigInteger nowBlockNumber = null;
            try {
                 nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();
            } catch (Exception e) {
                log.error("현재 블록 조회 실패{}", e.getMessage(), e);
            }

            // 컨펌 블록이 지난 트랜잭션 처리
            if (nowBlockNumber != null) {
                BigInteger confirmedBlock = nowBlockNumber.subtract(BigInteger.valueOf(chain.getRequiredConfirmations()));
                List<BridgeTransaction> txs = bridgeTransactionRepository.findConfirmedByChainAndType(chain, confirmedBlock);

                for (BridgeTransaction tx : txs) {
                    Optional<ExchangeRequest> exReqOpt = exchangeRequestRepository.findById(tx.getExchangeRequest().getId());
                    if (exReqOpt.isEmpty()) continue;
                    ExchangeRequest exReq = exReqOpt.get();

                    Optional<Transaction> txOpt;

                    try {
                        txOpt = httpWeb3.ethGetTransactionByHash(tx.getTransactionHash()).send().getTransaction();
                    } catch (Exception e) {
                        log.error("트랜잭션 조회 실패{}", e.getMessage(), e);
                        continue;
                    }

                    if (txOpt.isPresent()) {
                        tx.setStatus(TransactionStatus.CONFIRMED);
                        exReq.setBridgeStatus(BridgeStatus.IN_PROGRESS);
                    } else {
                        tx.setStatus(TransactionStatus.DROPPED);
                        exReq.setBridgeStatus(BridgeStatus.FAILED);
                    }

                    bridgeTransactionRepository.save(tx);
                    exchangeRequestRepository.save(exReq);
                }

            }

        }
    }
}

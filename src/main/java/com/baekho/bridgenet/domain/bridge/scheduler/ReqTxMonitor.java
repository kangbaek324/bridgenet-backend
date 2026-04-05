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
import com.baekho.bridgenet.global.common.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public void reqTxMonitor() {
        if (httpWeb3jMap.isEmpty()) return;

        List<Chain> chains = chainRepository.findAll();

        for (Chain chain : chains) {
            List<Web3j> web3jList = httpWeb3jMap.get(chain.getChainId());
            if (web3jList == null || web3jList.isEmpty()) continue;

            Web3j httpWeb3 = web3jList.get(rpcState.rpcCount(chain.getChainId()));

            // TODO: 중복 조회 제거 필요
            BigInteger nowBlockNumber = null;
            try {
                 nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();
            } catch (Exception e) {
                log.error("현재 블록 조회 실패{}", e.getMessage(), e);
            }

            // 컨펌 블록이 지난 트랜잭션 처리
            if (nowBlockNumber != null) {
                BigInteger confirmedBlock = nowBlockNumber.subtract(BigInteger.valueOf(chain.getRequiredConfirmations()));
                List<BridgeTransaction> txs = bridgeTransactionRepository.findConfirmedByChainAndType(chain, confirmedBlock, TransactionType.FROM);

                for (BridgeTransaction tx : txs) {
                    ExchangeRequest exReq = tx.getExchangeRequest();

                    // 트랜잭션 존재 여부 조회
                    Optional<Transaction> txOpt = Optional.empty();
                    try {
                        txOpt = httpWeb3.ethGetTransactionByHash(tx.getTransactionHash()).send().getTransaction();
                    } catch (Exception e) {
                        log.error("트랜잭션 조회 실패{}", e.getMessage(), e);
                    }

                    if (txOpt.isPresent()) {
                        // 존재하면 컨펌후 브릿징 상태 진행중으로 업데이트
                        tx.setStatus(TransactionStatus.CONFIRMED);
                        exReq.setBridgeStatus(BridgeStatus.IN_PROGRESS);
                    } else {
                        // 존재하지 않으면 드랍 후 실패처리
                        // 요청은 서버에서 다시 보낼 수 없음으로 실패처리 해야함
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

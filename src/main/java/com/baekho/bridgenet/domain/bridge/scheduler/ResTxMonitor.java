package com.baekho.bridgenet.domain.bridge.scheduler;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.bridge.entity.BridgeTransaction;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.domain.bridge.repository.BridgeTransactionRepository;
import com.baekho.bridgenet.domain.bridge.repository.ExchangeRequestRepository;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.enums.BridgeStatus;
import com.baekho.bridgenet.global.common.enums.TransactionStatus;
import com.baekho.bridgenet.global.common.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResTxMonitor {
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final BridgeTransactionRepository bridgeTransactionRepository;
    private final ChainRepository chainRepository;

    private final Map<Long, List<Web3j>> httpWeb3jMap;
    private final Map<Long, List<Bridge>> bridgeMap;
    private final RpcState rpcState;

    // TODO: 트랜잭션안에서 블록체인 조회 코드를 분리하기
    // resTx 처리 스케쥴러
    @Scheduled(fixedDelay = 1000 * 30)
    @Transactional
    public void resTxProcessor() {
        if (bridgeMap.isEmpty()) return;

        // 브릿지 상태가 IN_PROGRESS, 관리자의 승인이 난 요청 가져오기 (전송 처리 내역이 없는 요청)
        List<ExchangeRequest> targets = exchangeRequestRepository.findPendingRelayRequests();
        List<BridgeTransaction> txList = new ArrayList<>();

        for (ExchangeRequest exReq : targets) {
            Chain chain = exReq.getToChain();
            User user = exReq.getUser();

            List<Bridge> bridgeList = bridgeMap.get(chain.getChainId());
            if (bridgeList == null || bridgeList.isEmpty()) continue;
            Bridge bridge = bridgeList.get(rpcState.rpcCount(chain.getChainId()));

            // 자산 전송
            TransactionReceipt receipt = null;
            try {
                receipt = bridge.triggerPayout(
                        BigInteger.valueOf(exReq.getFromChain().getChainId()),
                        exReq.getIdInSmartContract(),
                        user.getAddress(),
                        exReq.getToValue()).send();
            } catch (Exception e) {
                log.error("Trigger Payout Error: {}", e.getMessage(), e);
            }
            if (receipt == null) continue;

            // DB 저장
            txList.add(BridgeTransaction.builder()
                    .exchangeRequest(exReq)
                    .chain(chain)
                    .transactionHash(receipt.getTransactionHash())
                    .type(TransactionType.TO)
                    .status(TransactionStatus.PENDING)
                    .processedBlock(receipt.getBlockNumber())
                    .build());
        };

        bridgeTransactionRepository.saveAll(txList);
    }

    // TODO: 트랜잭션안에서 블록체인 조회 코드를 분리하기
    // resTx 추적 스케쥴러
    @Scheduled(fixedDelay = 1000 * 30)
    @Transactional
    public void resTxMonitor() {
        if (httpWeb3jMap.isEmpty()) return;
        List<Chain> chains = chainRepository.findAll();

        for (Chain chain : chains) {
            List<Web3j> web3jList = httpWeb3jMap.get(chain.getChainId());
            if (web3jList == null || web3jList.isEmpty()) continue; // 비활성화 체인일 경우 이 조건으로 건너뜀

            Web3j httpWeb3 = web3jList.get(rpcState.rpcCount(chain.getChainId()));

            BigInteger nowBlockNumber = null;
            try {
                nowBlockNumber = httpWeb3.ethBlockNumber().send().getBlockNumber();
            } catch (Exception e) {
                log.error("현재 블록 조회 실패{}", e.getMessage(), e);
                continue;
            }

            // 컨펌 블록이 지난 트랜잭션 처리
            // 컨펌 수가 지난 TO, PENDING 트랜잭션을 조회
            BigInteger confirmedBlock = nowBlockNumber.subtract(BigInteger.valueOf(chain.getRequiredConfirmations()));
            List<BridgeTransaction> txs = bridgeTransactionRepository.findConfirmedByChainAndType(chain, confirmedBlock, TransactionType.TO);

            List<BridgeTransaction> updateTxList = new ArrayList<>();
            List<ExchangeRequest> updateExReqList = new ArrayList<>();

            for (BridgeTransaction tx : txs) {
                ExchangeRequest exReq = tx.getExchangeRequest();

                // 트랜잭션 조회
                TransactionReceipt receipt = null;
                try {
                    receipt = httpWeb3.ethGetTransactionReceipt(tx.getTransactionHash()).send().getResult();
                } catch (Exception e) {
                    log.error("트랜잭션 조회 실패{}", e.getMessage(), e);
                }

                if (receipt != null && receipt.isStatusOK()) {
                    // 성공이면 완료 처리
                    tx.setStatus(TransactionStatus.CONFIRMED);
                    exReq.setBridgeStatus(BridgeStatus.COMPLETED);
                } else {
                    // receipt 없거나 revert된 경우 드랍 처리
                    tx.setStatus(TransactionStatus.DROPPED);
                    int count = bridgeTransactionRepository.countByExchangeRequestAndStatus(exReq, TransactionStatus.DROPPED);

                    // 만약 드랍 트랜잭션이 3개 이상이면 FAILED 처리 (관리자 처리 필요)
                    // 이미 2개 + 현재 1개
                    if (count >= 2) {
                        exReq.setBridgeStatus(BridgeStatus.FAILED);
                    }
                }

                updateTxList.add(tx);
                updateExReqList.add(exReq);
            }

            bridgeTransactionRepository.saveAll(updateTxList);
            exchangeRequestRepository.saveAll(updateExReqList);
        }
    }
}

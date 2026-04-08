package com.baekho.bridgenet.global.blockchain;

import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainRecoverService {
    private final BlockchainEventService blockchainEventService;
    private final ChainRepository chainRepository;

    private final RpcState rpcState;
    private final Map<Long, Boolean> isRecoverMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;


    /**
     *  체인별 요청 값을 복구합니다.
     *  subscribeToContractEvents 함수 사용시 이 함수가 먼저 실행되어야합니다.
     * @param chain Chain
     * @param nowBlockNumber nowBlockNumber
     * @throws IOException
     * @throws InterruptedException
     */
    public void recoverEvent(Chain chain, BigInteger nowBlockNumber) throws IOException, InterruptedException {
        // 상태 저장
        isRecoverMap.put(chain.getChainId(), true);

        BigInteger lastBlockNumber = chain.getLastBlockNumber();

        // 맨처음 복구를 시작한 블록
        BigInteger recoverStartBlock = lastBlockNumber.add(BigInteger.ONE);

        long recoverValue = 500;

        // 매 시도마다 블록 시작값과 마지막 블록값
        BigInteger startBlockNumber = lastBlockNumber.add(BigInteger.valueOf(1));
        BigInteger finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue - 1));

        log.info("---- Start Recover Requested Event ChainId: {} ---", chain.getChainId());
        long start = System.currentTimeMillis();

        boolean isFinish = false;
        long chainId = chain.getChainId();

        while (true) {
            Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));

            if (finishBlockNumber.compareTo(nowBlockNumber) > 0) {
                finishBlockNumber = nowBlockNumber;
                isFinish = true;
            }

            showPercentLog(chain, recoverStartBlock, nowBlockNumber, finishBlockNumber);

            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(startBlockNumber),
                    DefaultBlockParameter.valueOf(finishBlockNumber),
                    chain.getSmartContractAddress()
            );

            filter.addSingleTopic(EventEncoder.encode(Bridge.REQUESTED_EVENT));

            EthLog ethLogs;
            try {
                ethLogs = httpWeb3.ethGetLogs(filter).send();
            } catch (Exception e) {
                log.warn("[Chain: {}] ethGetLogs 요청 실패 (블록 {}-{}), 재시도: {}", chain.getChainName(), startBlockNumber, finishBlockNumber, e.getMessage());
                Thread.sleep(3000);
                continue;
            }

            if (ethLogs.hasError()) {
                log.warn("[Chain: {}] ethGetLogs 에러 (블록 {}-{}), 재시도: {}", chain.getChainName(), startBlockNumber, finishBlockNumber, ethLogs.getError().getMessage());
                Thread.sleep(3000);
                continue;
            }

            List<EthLog.LogResult> logs = ethLogs.getLogs();
            if (logs != null) {
                for (EthLog.LogResult<?> logResult : logs) {
                    Log bcLog = (Log) logResult.get();
                    Bridge.RequestedEventResponse e = Bridge.getRequestedEventFromLog(bcLog);

                    blockchainEventService.saveRequest(e);
                }
            }

            if (isFinish) {
                chain.setLastBlockNumber(finishBlockNumber);
                chainRepository.save(chain);

                break;
            }
            else {
                startBlockNumber = finishBlockNumber.add(BigInteger.valueOf(1));
                finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue - 1));

                // RPC 429 (To many Request) 해결
                Thread.sleep(250);
            }
        }

        long end = System.currentTimeMillis();
        log.info("---- Success Recover Requested Event ----");
        log.info("Time Taken: {}ms", end - start);

        // 상태 저장
        isRecoverMap.put(chain.getChainId(), false);
    }

    private static void showPercentLog(
            Chain chain,
            BigInteger recoverStartBlock,
            BigInteger recoverEndBlockNumber,
            BigInteger nowRecoverBlockNumber
    ) {
        BigInteger total = recoverEndBlockNumber.subtract(recoverStartBlock);
        BigInteger progressed = nowRecoverBlockNumber.subtract(recoverStartBlock);

        double percent;
        if (total.signum() <= 0) {
            percent = 100.0;
        } else {
            percent = progressed
                    .max(BigInteger.ZERO)
                    .min(total)
                    .multiply(BigInteger.valueOf(100))
                    .doubleValue() / total.doubleValue();
        }

        System.out.printf(
                "\r[Recovering %s] Now: %s | End: %s (%.2f%%)\n",
                chain.getChainName(),
                nowRecoverBlockNumber.toString(),
                recoverEndBlockNumber.toString(),
                percent
        );
    }
}

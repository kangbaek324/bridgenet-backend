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
    private final RpcState rpcState;
    private final BlockchainEventService blockchainEventService;
    private final ChainRepository chainRepository;

    private final Map<Long, Boolean> isRecoverMap;
    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;


    /*
     * @TODO 등록시 블록체인에서 이미 처리된 요청인지 확인하는 로직 추가 필요
     *  나중에 DB 날아갔을때 recover 함수를 실행시키면 미처리 요청으로 들어가기 때문
     */
    /**
     *      *  체인별 요청 값을 복구합니다.
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

        long recoverValue = 1500;

        // 매 시도마다 블록 시작값과 마지막 블록값
        BigInteger startBlockNumber = lastBlockNumber.add(BigInteger.valueOf(1));
        BigInteger finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue));

        log.info("---- Start Recover Requested Event ChainId: {} ---\n", chain.getChainId());
        long start = System.currentTimeMillis();

        boolean isFinish = false;
        long chainId = chain.getChainId();

        while (true) {
            Bridge bridge = bridgeMap.get(chainId).get(rpcState.rpcCount(chainId));
            Web3j httpWeb3 = httpWeb3jMap.get(chainId).get(rpcState.rpcCount(chainId));

            if (finishBlockNumber.compareTo(nowBlockNumber) > 0) {
                finishBlockNumber = nowBlockNumber;
                isFinish = true;

                System.out.println("\n");
            }

            showPercentLog(chain, recoverStartBlock, nowBlockNumber, finishBlockNumber);

            EthFilter filter = new EthFilter(
                    DefaultBlockParameter.valueOf(startBlockNumber),
                    DefaultBlockParameter.valueOf(finishBlockNumber),
                    bridge.getContractAddress()
            );

            filter.addSingleTopic(EventEncoder.encode(Bridge.REQUESTED_EVENT));
            EthLog ethLogs = httpWeb3.ethGetLogs(filter).send();

            for (EthLog.LogResult logResult : ethLogs.getLogs()) {
                Log bcLog = (Log) logResult.get();
                Bridge.RequestedEventResponse e = Bridge.getRequestedEventFromLog(bcLog);

                blockchainEventService.saveRequest(e);
            }

            if (isFinish) {
                break;
            }
            else {
                startBlockNumber = finishBlockNumber.add(BigInteger.valueOf(1));
                finishBlockNumber = startBlockNumber.add(BigInteger.valueOf(recoverValue));

                // RPC 429 (To many Request) 해결
                Thread.sleep(1000);
            }
        }

        chain.setLastBlockNumber(finishBlockNumber);
        chainRepository.save(chain);

        long end = System.currentTimeMillis();
        log.info("---- Success Recover Requested Event ----");
        System.out.println("Time Taken: " + (end - start) + "ms");

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
                "\r[Recovering %s] Now: %s | End: %s (%.2f%%)",
                chain.getChainName(),
                nowRecoverBlockNumber.toString(),
                recoverEndBlockNumber.toString(),
                percent
        );
    }
}

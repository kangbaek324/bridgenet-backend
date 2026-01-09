package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.blockchain.BlockchainService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.Protocol;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChainRpcService {
    private final RpcRepository rpcRepository;
    private final ChainRepository chainRepository;
    private final BlockchainService blockchainService;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;

    public List<RpcResponseDTO> getRpcs(Long chainId, Protocol protocol) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        List<Rpc> rpcsDB;
        if (protocol == null) {
            rpcsDB = rpcRepository.findAllByChain(chain);
        }
        else {
            rpcsDB = rpcRepository.findAllByChainAndProtocol(chain, protocol);
        }

        List<RpcResponseDTO> res = new ArrayList<>();

        rpcsDB.forEach(rpc -> {
            res.add(
                new RpcResponseDTO(
                    rpc.getId(),
                    rpc.getServiceName(),
                    rpc.getUrl(),
                    rpc.getProtocol()
                )
            );
        });

        return res;
    }

    // @TODO 중복 칼럼 예외 처리해야됨
    public RpcAddResponseDTO addRpc(RpcAddRequestDTO dto, Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_MUST_DEACTIVATE);

        Rpc rpc = Rpc.builder()
                .chain(chain)
                .serviceName(dto.getServiceName())
                .url(dto.getUrl())
                .protocol(dto.getProtocol())
                .build();
        rpcRepository.save(rpc);

        return new RpcAddResponseDTO(
                rpc.getId(),
                rpc.getServiceName(),
                rpc.getUrl(),
                rpc.getProtocol()
        );
    }
}

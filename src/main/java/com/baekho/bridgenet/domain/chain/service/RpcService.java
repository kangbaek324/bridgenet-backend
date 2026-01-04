package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.ChainRpcGroupDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.blockchain.BlockchainService;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.code.RpcErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.common.exception.RpcException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpcService {
    private final RpcRepository rpcRepository;
    private final ChainRepository chainRepository;
    private final BlockchainService blockchainService;
    private final RpcState rpcState;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;

    public Map<Long, ChainRpcGroupDTO> getRpcs() {
        List<Rpc> rpcsDB = rpcRepository.findAll();
        Map<Long, ChainRpcGroupDTO> result = new TreeMap<>();

        rpcsDB.forEach(rpc -> {
            Long chainId = rpc.getChain().getChainId();

            result.computeIfAbsent(
                    chainId,
                    k -> new ChainRpcGroupDTO(
                            rpc.getChain().getChainName(),
                            new ArrayList<>()
                    )
            ).getRpcs().add(
                    new RpcResponseDTO(
                            rpc.getServiceName(),
                            rpc.getUrl(),
                            rpc.getProtocol()
                    )
            );
        });

        return result;
    }

    // @TODO 중복 칼럼 예외 처리해야됨
    public RpcAddResponseDTO addRpc(RpcAddRequestDTO dto, Long chainId) {
        Chain chain = chainRepository.findByChainId(chainId)
                .orElseThrow(() -> new ChainException(ChainErrorCode.CHAIN_NOT_FOUND));

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

    public RpcUpdateResponseDTO updateRpc(RpcUpdateRequestDTO dto, Long id) {
        Rpc rpc = rpcRepository.findById(id)
                .orElseThrow(() -> new RpcException(RpcErrorCode.RPC_NOT_FOUND));

        Chain chain = rpc.getChain();
        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_MUST_DEACTIVATE);

        rpc.setServiceName(dto.getServiceName());
        rpc.setUrl(dto.getUrl());
        rpc.setProtocol(dto.getProtocol());

        rpcRepository.save(rpc);

        return new RpcUpdateResponseDTO(
                rpc.getServiceName(),
                rpc.getUrl(),
                rpc.getProtocol()
        );
    }

    /**
     * 새로운 HTTP RPC를 등록합니다.
     * @param chain
     * Chain 객체
     * @param rpc
     * Rpc 객체
     */
    public void createRpc(Chain chain, Rpc rpc) {
        Web3j web3j = Web3j.build(new HttpService(rpc.getUrl()));

        httpWeb3jMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(web3j);

        Bridge bridge = blockchainService.createBridgeObject(chain, web3j);
        bridgeMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(bridge);
    }
}

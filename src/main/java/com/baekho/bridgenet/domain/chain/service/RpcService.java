package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.blockchain.contract.SmartContractService;
import com.baekho.bridgenet.global.blockchain.contract.bridge.Bridge;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.code.RpcErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.common.exception.RpcException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RpcService {
    private final RpcRepository rpcRepository;
    private final SmartContractService smartContractService;

    private final Map<Long, List<Bridge>> bridgeMap;
    private final Map<Long, List<Web3j>> httpWeb3jMap;

    public RpcResponseDTO getRpc(Long id) {
        Rpc rpc = rpcRepository.findById(id)
                .orElseThrow(() -> new RpcException(RpcErrorCode.RPC_NOT_FOUND));

        return new RpcResponseDTO(
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
                rpc.getId(),
                rpc.getServiceName(),
                rpc.getUrl(),
                rpc.getProtocol()
        );
    }

    public void deleteRpc(Long id) {
        Rpc rpc = rpcRepository.findById(id)
                .orElseThrow(() -> new RpcException(RpcErrorCode.RPC_NOT_FOUND));

        Chain chain = rpc.getChain();
        if (chain.isStatus()) throw new ChainException(ChainErrorCode.CHAIN_MUST_DEACTIVATE);

        rpcRepository.delete(rpc);
    }

    /**
     * 새로운 HTTP RPC를 등록합니다.
     * @param chain
     * Chain 객체
     * @param rpc
     * Rpc 객체
     */
    public void createHttpRpc(Chain chain, Rpc rpc) {
        Web3j web3j = Web3j.build(new HttpService(rpc.getUrl()));

        httpWeb3jMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(web3j);

        Bridge bridge = smartContractService.createBridgeObject(chain, web3j);
        bridgeMap
                .computeIfAbsent(chain.getChainId(), k -> new ArrayList<>())
                .add(bridge);
    }
}

package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.request.RpcAddRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.ChainRpcGroupDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcAddResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.ChainRepository;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.blockchain.RpcState;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.enums.RpcUpdateDirection;
import com.baekho.bridgenet.global.common.exception.ChainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RpcService {
    private final RpcRepository rpcRepository;
    private final ChainRepository chainRepository;
    private final RpcState rpcState;

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

        rpcState.updateRpcNumber(chainId, 1, RpcUpdateDirection.INCREASE);

        return new RpcAddResponseDTO(
                rpc.getServiceName(),
                rpc.getUrl(),
                rpc.getProtocol()
        );
    }
}

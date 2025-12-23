package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.response.ChainRpcGroupDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RpcService {
    private final RpcRepository rpcRepository;

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
                            rpc.getHttp(),
                            rpc.getWs()
                    )
            );
        });

        return result;
    }

}

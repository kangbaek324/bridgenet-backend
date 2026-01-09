package com.baekho.bridgenet.domain.chain.service;

import com.baekho.bridgenet.domain.chain.dto.request.RpcUpdateRequestDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcResponseDTO;
import com.baekho.bridgenet.domain.chain.dto.response.RpcUpdateResponseDTO;
import com.baekho.bridgenet.domain.chain.entity.Chain;
import com.baekho.bridgenet.domain.chain.entity.Rpc;
import com.baekho.bridgenet.domain.chain.repository.RpcRepository;
import com.baekho.bridgenet.global.common.code.ChainErrorCode;
import com.baekho.bridgenet.global.common.code.RpcErrorCode;
import com.baekho.bridgenet.global.common.exception.ChainException;
import com.baekho.bridgenet.global.common.exception.RpcException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RpcService {
    private final RpcRepository rpcRepository;

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
}

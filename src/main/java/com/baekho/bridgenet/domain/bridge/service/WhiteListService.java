package com.baekho.bridgenet.domain.bridge.service;

import com.baekho.bridgenet.domain.bridge.dto.WhiteListRequestDTO;
import com.baekho.bridgenet.domain.bridge.dto.WhiteListResponseDTO;
import com.baekho.bridgenet.global.common.code.WhiteListErrorCode;
import com.baekho.bridgenet.domain.auth.entity.Users;
import com.baekho.bridgenet.global.common.exception.WhiteListException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.baekho.bridgenet.global.blockchain.bridgenet.Bridge;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

@Service
@RequiredArgsConstructor
public class WhiteListService {
    private final Bridge sepoliaBridge;
    private final Bridge amoyBridge;

    public WhiteListResponseDTO setWhiteList(WhiteListRequestDTO dto) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) authentication.getPrincipal();

        Long chainId = dto.getChainId();
        TransactionReceipt receipt;

        if (chainId == 11155111) {
            receipt = sepoliaBridge.setWhiteList(user.getAddress(), true).send();
        }
        else if (chainId == 80002) {
            receipt = amoyBridge.setWhiteList(user.getAddress(), true).send();
        }
        else {
            throw new WhiteListException(WhiteListErrorCode.UNKNOWN_CHAIN_ID);
        }

        return new WhiteListResponseDTO(receipt.getTransactionHash());
    }
}

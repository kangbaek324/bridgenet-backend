package com.baekho.bridgenet.domain.chain.repository.projection;

import com.baekho.bridgenet.global.common.enums.ChainStatus;

public interface ChainStatusProjection {
    ChainStatus getStatus();
}


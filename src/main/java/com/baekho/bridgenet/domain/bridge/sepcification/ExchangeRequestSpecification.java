package com.baekho.bridgenet.domain.bridge.sepcification;

import com.baekho.bridgenet.domain.auth.entity.User;
import com.baekho.bridgenet.domain.bridge.entity.ExchangeRequest;
import com.baekho.bridgenet.global.common.enums.ApproveStatus;
import org.springframework.data.jpa.domain.Specification;

public interface ExchangeRequestSpecification {
    public static Specification<ExchangeRequest> hasChainId(Long chainId) {
        return (root, query, cb) ->
                cb.or(
                        cb.equal(root.get("toChain").get("chainId"), chainId),
                        cb.equal(root.get("fromChain").get("chainId"), chainId)
                );
    }

    public static Specification<ExchangeRequest> hasFromChainId(Long chainId) {
        return (root, query, cb) -> cb.equal(root.get("fromChain").get("chainId"), chainId);
    }

    public static Specification<ExchangeRequest> hasToChainId(Long chainId) {
        return (root, query, cb) -> cb.equal(root.get("toChain").get("chainId"), chainId);
    }

    public static Specification<ExchangeRequest> hasStatus(ApproveStatus status) {
        return (root, query, cb) -> cb.equal(root.get("approveStatus"), status);
    }

    public static Specification<ExchangeRequest> withUser(User user) {
        return (root, query, cb) -> {
            if (user == null) return cb.conjunction();
            return cb.equal(root.get("user"), user);
        };
    }
}

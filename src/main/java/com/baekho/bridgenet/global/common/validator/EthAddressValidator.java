package com.baekho.bridgenet.global.common.validator;

import com.baekho.bridgenet.global.common.annotation.IsEthAddress;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.web3j.crypto.WalletUtils;

public class EthAddressValidator implements ConstraintValidator<IsEthAddress, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && WalletUtils.isValidAddress(value);
    }
}
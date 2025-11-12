package com.baekho.bridgenet.global.common.annotation;

import com.baekho.bridgenet.global.common.validator.EthAddressValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.web3j.crypto.WalletUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EthAddressValidator.class)
public @interface IsEthAddress {
    String message() default "유효하지 않은 이더리움 주소입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}


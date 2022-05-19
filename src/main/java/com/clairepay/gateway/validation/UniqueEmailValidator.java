package com.clairepay.gateway.validation;

import com.clairepay.gateway.repository.PayerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    @Autowired
    PayerRepository payerRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (payerRepository != null && payerRepository.findByEmail(value).isPresent()) {
            return false;
        }
        return true;
    }
}

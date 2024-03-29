package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {


    Optional<PaymentMethod> findByMethodNameIgnoreCase(String methodName);

}

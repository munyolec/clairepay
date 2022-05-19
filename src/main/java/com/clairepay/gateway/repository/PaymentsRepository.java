package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;


public interface PaymentsRepository extends JpaRepository<Payments, Long> {

    @Query("SELECT p FROM Payments p WHERE p.payer.payerId = ?1")
    Collection<Payments> findByPayerId(Long payerId);

    @Query("SELECT p FROM Payments p WHERE p.merchant.apiKey= ?1")
    Collection<Payments> findByPayerApiKey(String apiKey);

    Optional<Payments> findByReferenceId(String referenceId);
}

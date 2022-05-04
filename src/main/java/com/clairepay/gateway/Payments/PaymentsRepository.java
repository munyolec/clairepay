package com.clairepay.gateway.Payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;


public interface PaymentsRepository extends JpaRepository<Payments, Long> {

    @Query("SELECT p FROM Payments p WHERE p.payer.payerId = ?1")
    Collection<Payments> findByPayerId(Long payerId);
}

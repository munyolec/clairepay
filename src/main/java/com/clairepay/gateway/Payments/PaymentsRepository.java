package com.clairepay.gateway.Payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<Payments, Long> {
//    List<PaymentsDTO> findByPayerId(Long payerId);

    @Query("SELECT p FROM Payments p WHERE p.payer.payerId = ?1")
    Optional<Payments> findByPayerId(Long payerId);
}

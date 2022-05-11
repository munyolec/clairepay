package com.clairepay.gateway.Payer;

import com.clairepay.gateway.Payments.Payments;
import com.clairepay.gateway.Payments.PaymentsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayerRepository extends JpaRepository<Payer, Long> {

    @Query("" +
            "SELECT CASE WHEN COUNT(p) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM Payer p " +
            "WHERE p.email = ?1"
    )

    Boolean selectExistsByEmail(String email);

    @Query("SELECT p FROM Payer p WHERE p.payerId = ?1")
    Optional<Payer> findById(Long payerId);

    Optional<Payer> findByEmail(String email);


}

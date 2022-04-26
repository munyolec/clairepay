package com.clairepay.gateway.Payer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PayerRepository extends JpaRepository<Payer, Long> {

    @Query("" +
            "SELECT CASE WHEN COUNT(p) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM Payer p " +
            "WHERE p.email = ?1"
    )
    Boolean selectExistsByEmail(String email);
}

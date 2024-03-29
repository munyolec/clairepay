package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayerRepository extends JpaRepository<Payer, Long> {

    @Query("SELECT p FROM Payer p WHERE p.payerId = ?1")
    Optional<Payer> findById(Long payerId);

    Optional<Payer> findByEmail(String email);


}

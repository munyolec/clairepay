package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {



    Optional<Merchant> findByApiKey(String apiKey);

    @Query("SELECT m FROM Merchant m WHERE m.merchantId = ?1")
    Optional<Merchant> findById(Long merchantId);

    Optional<Merchant> findByEmail(String email);
}

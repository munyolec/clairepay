package com.clairepay.gateway.Merchant;

import com.clairepay.gateway.Payer.Payer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    @Query("" +
            "SELECT CASE WHEN COUNT(m) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM Merchant m " +
            "WHERE m.apiKey = ?1"
    )
    Boolean selectExistsByApiKey(String apiKey);

    @Query("SELECT m FROM Merchant m WHERE m.merchantId = ?1")
    Optional<Merchant> findById(Long merchantId);
}

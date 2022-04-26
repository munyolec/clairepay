package com.clairepay.gateway.Merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    @Query("" +
            "SELECT CASE WHEN COUNT(m) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM Merchant m " +
            "WHERE m.apiKey = ?1"
    )
    Boolean selectExistsByApiKey(String apiKey);
}

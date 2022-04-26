package com.clairepay.gateway.Merchant;

import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MerchantConfig {
    @Bean
    CommandLineRunner commandLineMerchant(
            MerchantRepository repository
    ){
        return args -> {
            Merchant one = new Merchant(
                   "Malek",
                    "Waters",
                    "malek@gmail.com",
                    "0700256345"
            );

            repository.saveAll(List.of(one));
        };
    }

}

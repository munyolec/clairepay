package com.clairepay.gateway.Config;

import com.clairepay.gateway.models.Merchant;
import com.clairepay.gateway.repository.MerchantRepository;
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
                    "ef99a898-a515-414d-9655-5bf1a0fede0d",
                   "Malek",
                    "Waters",
                    "malek@gmail.com",
                    "0700256345"
            );
            Merchant two = new Merchant(
                    "bd68fc83-6b07-4c87-9142-e49081358ffa",
                    "Walter",
                    "Richards",
                    "richwalt@gmail.com",
                    "0700256345"
            );

            repository.saveAll(List.of(one, two));
        };
    }

}

package com.clairepay.gateway.config;

import com.clairepay.gateway.models.PaymentMethod;
import com.clairepay.gateway.repository.PaymentMethodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PaymentMethodsConfig {
    @Bean
    CommandLineRunner commandLineRunnerPM(
            PaymentMethodRepository repository
    ) {
        return args -> {
            PaymentMethod card = new PaymentMethod(
                    "Card"

            );

            PaymentMethod mpesa = new PaymentMethod(
                    "MobileMoney"

            );

            repository.saveAll(List.of(card, mpesa));
        };
    }
}


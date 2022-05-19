package com.clairepay.gateway.PaymentMethod;

import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
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


package com.clairepay.gateway.CardDetails;

import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class CardDetailsConfig {
    @Bean
    CommandLineRunner commandLineRunnerCard(
            CardDetailsRepository repository
    ){
        return args -> {
            CardDetails one = new CardDetails(
                    "12345678902423",
                    "05/22"
            );

            CardDetails two = new CardDetails(
                    "12345364743223",
                    "06/22"

            );

            repository.saveAll(List.of(one, two));
        };
    }
}

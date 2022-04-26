package com.clairepay.gateway.Payer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
@Configuration
public class PayerConfig {


        @Bean
        CommandLineRunner commandLineRunnerPayer(
                PayerRepository repository
        ){
            return args -> {
                Payer one = new Payer(
                        "Claire",
                        "Munyole",
                        "munyolec@gmail.com",
                        "0718892704"
                );

                Payer two = new Payer(
                        "Brad",
                        "Pitt",
                        "brad@gmail.com",
                        "0702497542"
                );

                repository.saveAll(List.of(one, two));
            };
        }
    }


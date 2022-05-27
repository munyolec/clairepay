package com.clairepay.gateway.repository;

import com.clairepay.gateway.models.Payer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class PayerRepositoryTest {
    @Autowired
    private PayerRepository payerRepositoryUnderTest;

    @AfterEach
    void tearDown() {
        payerRepositoryUnderTest.deleteAll();
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void itShouldCheckWhetherPayerExistsById() {
        //given
        Payer payer = new Payer("Jam", "Moha","jamila@gmail.com","0702222222");
        payerRepositoryUnderTest.save(payer);
        //when
        Optional<Payer> exists = payerRepositoryUnderTest.findById(payer.getPayerId());
        //then
        assertTrue(exists.isPresent());
    }

    @Test
    void itShouldCheckWhetherPayerExistsByEmail() {
        //given
        String email = "jamila1@gmail.com";
        Payer payer = new Payer("Jam", "Moha",email,"0702222222");
        payerRepositoryUnderTest.save(payer);
        //when
        Optional<Payer> exists = payerRepositoryUnderTest.findByEmail(email);
        //then
        assertTrue(exists.isPresent());
    }
}
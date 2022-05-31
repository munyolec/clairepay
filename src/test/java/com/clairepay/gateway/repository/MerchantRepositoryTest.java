package com.clairepay.gateway.repository;

import com.clairepay.gateway.AbstractTest;
import com.clairepay.gateway.models.Merchant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MerchantRepositoryTest  {

    @Autowired
    private MerchantRepository merchantRepositoryUnderTest;


    @AfterEach
    void tearDown() {
        merchantRepositoryUnderTest.deleteAll();
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    void findByApiKey() {
        //given
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0e", "Malek",
                "Waters", "malek@gmail.com", "0700256345");
        merchantRepositoryUnderTest.save(merchant);
        //when
        Optional<Merchant> exists = merchantRepositoryUnderTest.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0e");
        //then
        assertTrue(exists.isPresent());
    }

    @Test
    void findById() {
        //given
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0e", "Malek",
                "Waters", "malek@gmail.com", "0700256345");
        merchantRepositoryUnderTest.save(merchant);
        //when
        Optional<Merchant> exists = merchantRepositoryUnderTest.findById(1L);
        //then
        assertTrue(exists.isPresent());
    }

    @Test
    void findByEmail() {
        //given
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0e", "Malek",
                "Waters", "malek4@gmail.com", "0700256345");
        merchantRepositoryUnderTest.save(merchant);
        //when
        Optional<Merchant> exists = merchantRepositoryUnderTest.findByEmail("malek4@gmail.com");
        //then
        assertTrue(exists.isPresent());
    }
    }

package com.clairepay.gateway.service;

import com.clairepay.gateway.AbstractTest;
import com.clairepay.gateway.models.Merchant;
import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest  {
    @Mock
    private MerchantRepository merchantRepository;
    private MerchantService merchantServiceUnderTest;

    @BeforeEach
    public void setUp() {
        this.merchantServiceUnderTest = new MerchantService(this.merchantRepository);
    }

    @Test
    void getMerchantList() {
        //when
        merchantServiceUnderTest.getMerchantList();
        //then
        verify(merchantRepository).findAll();
    }


    @Test
    void getMerchantById() {
        //given
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0e", "Malek",
                "Waters", "malek@gmail.com", "0700256345");
        merchantRepository.save(merchant);
        //when
        merchantServiceUnderTest.getMerchant(1L);
        //then
        verify(merchantRepository).findById(1L);

    }
}
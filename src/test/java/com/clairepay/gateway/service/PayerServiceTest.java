package com.clairepay.gateway.service;

import com.clairepay.gateway.dto.PayerDTO;
import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.repository.PayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PayerServiceTest {
    @Mock private PayerRepository payerRepository;
    private PayerService payerServiceUnderTest;

    @BeforeEach
    void setUp(){
        this.payerServiceUnderTest = new PayerService(this.payerRepository);
    }

    @Test
    void canGetAllPayers() {
        //when
        payerServiceUnderTest.getAllPayers();
        //then
        verify(payerRepository).findAll();
    }

    @Test
    void canGetPayerByEmail() {
        //given
        String email = "jamila1@gmail.com";
        Payer payer = new Payer("Jam", "Moha",email,"0702222222");
        payerServiceUnderTest.addNewPayer(payer);
        //when
        payerServiceUnderTest.getPayerByEmail(email);
        //then
        verify(payerRepository).findByEmail(email);
    }

    @Test
    void addNewPayer() {
        //given
        String email = "jamila1@gmail.com";
        Payer payer = new Payer("Jam", "Moha",email,"0702222222");
        //when
        payerServiceUnderTest.addNewPayer(payer);
        //then
        ArgumentCaptor<Payer> payerArgumentCaptor = ArgumentCaptor.forClass(Payer.class);
        verify(payerRepository).save(payerArgumentCaptor.capture());

        Payer capturedPayer = payerArgumentCaptor.getValue();
        assertThat(capturedPayer).isEqualTo(payer);
    }

}
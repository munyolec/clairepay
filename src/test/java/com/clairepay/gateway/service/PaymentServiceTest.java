package com.clairepay.gateway.service;

import com.clairepay.gateway.dto.*;

import com.clairepay.gateway.error.InvalidParameterException;

import com.clairepay.gateway.models.*;

import com.clairepay.gateway.repository.MerchantRepository;
import com.clairepay.gateway.repository.PayerRepository;
import com.clairepay.gateway.repository.PaymentMethodRepository;
import com.clairepay.gateway.repository.PaymentsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class PaymentServiceTest {
    @Mock private PayerRepository payerRepository;
    @Mock private PaymentsRepository paymentsRepository;
    @Mock private MerchantRepository merchantRepository;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private  RabbitTemplate rabbitTemplate;
    @Mock private ConsumeChargeCardService consumeChargeCardService;
    @Autowired
    private PaymentService paymentServiceUnderTest;

    @BeforeEach
    void setUp() {
        this.paymentServiceUnderTest =
                new PaymentService(this.payerRepository, this.merchantRepository, this.paymentsRepository,
                        this.paymentMethodRepository, this.rabbitTemplate, this.consumeChargeCardService);

    }


    @Test
    void getAllPayments() {
        //when
        paymentServiceUnderTest.getAllPayments();
        //then
        verify(paymentsRepository).findAll();
    }

    @Test
    void getPayerPayment() {
        //given
         paymentServiceUnderTest
                .createNewPayer("Jam", "Mo","m@gmail.com","0702222222");
        //then
        assertTrue(paymentServiceUnderTest.getPayerPayment(1L).isEmpty());

    }

    @Test
    void publishToMpesaQueue() {
        paymentServiceUnderTest.publishToMpesaQueue("j","mo","jmo@gmail.com",
                "073231231",50);
    }

    @Test
    void createNewPayer() {
        //given
        String email = "jamila1@gmail.com";
        //when
        Payer payer = paymentServiceUnderTest.createNewPayer("Jam", "Mo",email,"0702222222");
        //then
        ArgumentCaptor<Payer> payerArgumentCaptor = ArgumentCaptor.forClass(Payer.class);
        verify(payerRepository).save(payerArgumentCaptor.capture());

        Payer capturedPayer = payerArgumentCaptor.getValue();
        assertThat(capturedPayer).isEqualTo(payer);
    }

    @Test
    void createExpiry() {
        Expiry expiry = paymentServiceUnderTest.createExpiry(2020, 12);
        assertThat(expiry.getMonth()).isEqualTo(12);
    }

    @Test
    void itShouldCreateCard() {
        Expiry expiry = new Expiry(2020, 12);
        Card card = paymentServiceUnderTest.createCard(278,234567891,expiry);
        assertThat(card.getCvv()).isEqualTo(278);
    }

    @Test
    void validateExpiryYear() {
        Expiry expiry = paymentServiceUnderTest.createExpiry(12, 2020);
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class, () -> paymentServiceUnderTest
                        .validateExpiry(expiry.getYear(),expiry.getMonth()), "card has expired");
        assertTrue(thrown.getMessage().contains("expired"));
    }
    @Test
    void itShouldValidateExpiryMonth() {
        Expiry expiry = paymentServiceUnderTest.createExpiry(2022, 4);
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class, () -> paymentServiceUnderTest
                        .validateExpiry(expiry.getYear(),expiry.getMonth()), "card has expired");
        assertTrue(thrown.getMessage().contains("expired"));
    }

    @Test
    void itShouldThrowExceptionWhenYearisInvalid() {
        int year = 20200;
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateYear(year),
                "invalid year");
        assertTrue(thrown.getMessage().contains("year"));
    }

    @Test
    void itShouldThrowExceptionWhenCVVisInvalid() {
        int cvv=233223;
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateCVV(cvv),
                "Invalid cvv");
        assertTrue(thrown.getMessage().contains("cvv"));
    }

    @Test
    void itShouldThrowExceptionWhenCardNumberIsNotValid(){
        long cardNumber = 222;
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateCardNumber(cardNumber),
                "Invalid card number");
        assertTrue(thrown.getMessage().contains("card"));
    }

    @Test
    void itShouldThrowExceptionWhenAmountLessThanOne() {
        int amount = -1;
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateAmount(amount),
                "Amount cannot be less than 1");
        assertTrue(thrown.getMessage().contains("1"));
    }
    @Test
    void itShouldThrowExceptionWhenAmountLessMoreThanOneMillion() {
        int amount = 10000001;
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateAmount(amount),
                "Amount cannot be greater than 1 000 000");
        assertTrue(thrown.getMessage().contains("greater"));
    }

    @Test
    void itShouldThrowExceptionWhenCurrencyNotSupported() {
        String currency = "KIU";
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateCurrency(currency),
                "Currency not supported");
        assertTrue(thrown.getMessage().contains("Currency"));
    }

    @Test
    void itShouldThrowExceptionWhenCountryNotSupported() {
        String country = "USA";
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateCountries(country),
                "Country not supported");
        assertTrue(thrown.getMessage().contains("Country"));
    }

    @Test
    void itShouldThrowExceptionWhenReferenceIdIsDuplicate() {
    }

    @Test
    void shouldReturnMerchantWhenTheMerchantExistsInDb(){
        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
                "Walters","m@walters.com","0722897644");
//        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
//                "Walters","m@walters.com","0722897644");
        when(merchantRepository.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0d")).thenReturn(Optional.of(merchant1));
        assertTrue(paymentServiceUnderTest.validateMerchant("ef99a898-a515-414d-9655-5bf1a0fede0d").equals(merchant1));
    }

    @Test
    void shouldThrowExceptionWhenMerchantDoesNotExist(){
        String apiKey = "ef99a898-a515-414d-9655-5bf1a0fede0d";
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validateMerchant(apiKey),
                "merchant not found");
        assertTrue(thrown.getMessage().contains("merchant"));
    }

    @Test
    void shouldReturnPaymentMethodWhenMethodExistsInDb(){
        PaymentMethod paymentMethod= new PaymentMethod("card");
        when(paymentMethodRepository.findByMethodNameIgnoreCase("card")).thenReturn(Optional.of(paymentMethod));
        assertTrue(paymentServiceUnderTest.
                validatePaymentMethod("card").equals(paymentMethod));
    }
    @Test
    void shouldThrowExceptionWhenPaymentMethodDoesNotExist(){
        String paymentMethodName = "bank";
        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest.validatePaymentMethod(paymentMethodName),
                "payment method not available");
        assertTrue(thrown.getMessage().contains("method"));
    }


    @Test
    void shouldSavePaymentToDbWhenRequestIsValid(){
        PaymentMethod paymentMethod= new PaymentMethod("card");
        when(paymentMethodRepository.findByMethodNameIgnoreCase(paymentMethod.getMethodName()))
                .thenReturn(Optional.of(paymentMethod));
        Payer payer = new Payer("Me","myself","myself@gmail.com","0718876399");
        Card card = new Card(234,23456789,new Expiry(2022,12));
        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
                "Walters","m@walters.com","0722897644");
        when(merchantRepository.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0d")).thenReturn(Optional.of(merchant1));

        PaymentRequest paymentRequest = new PaymentRequest("niwjenfiwe","KEN","KES", 20,
                paymentMethod.convertPaymentEntityToDTO(),payer.convertPayerEntityToDTO(),card);
        paymentServiceUnderTest.paymentProcessor(paymentRequest,"ef99a898-a515-414d-9655-5bf1a0fede0d");
        verify(paymentsRepository).findByReferenceId("niwjenfiwe");

    }
    @Test
    void shouldThrowErrorWhenPaymentMethodIsMissing(){
        Payer payer = new Payer("Me","myself","myself@gmail.com","0718876399");
        Card card = new Card(234,23456789,new Expiry(2022,12));
        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
                "Walters","m@walters.com","0722897644");
        when(merchantRepository.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0d")).thenReturn(Optional.of(merchant1));
        PaymentRequest paymentRequest = new PaymentRequest("niwjenfiwe","KEN","KES", 20,payer.convertPayerEntityToDTO(),card);

        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest
                        .paymentProcessor(paymentRequest,"ef99a898-a515-414d-9655-5bf1a0fede0d"),
                "payment method is required");
        assertTrue(thrown.getMessage().contains("required"));

    }
    @Test
    void shouldThrowErrorWhenNoCardDetailsAreProvidedForCardPaymentMethod(){
        PaymentMethod paymentMethod= new PaymentMethod("card");
        when(paymentMethodRepository.findByMethodNameIgnoreCase(paymentMethod.getMethodName()))
                .thenReturn(Optional.of(paymentMethod));
        Payer payer = new Payer("Me","myself","myself@gmail.com","0718876399");
        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
                "Walters","m@walters.com","0722897644");
        when(merchantRepository.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0d")).thenReturn(Optional.of(merchant1));
        PaymentRequest paymentRequest = new PaymentRequest("niwjenfiwe","KEN","KES", 20,
                paymentMethod.convertPaymentEntityToDTO(),payer.convertPayerEntityToDTO());

        InvalidParameterException thrown = assertThrows(
                InvalidParameterException.class,() -> paymentServiceUnderTest
                        .paymentProcessor(paymentRequest,"ef99a898-a515-414d-9655-5bf1a0fede0d"),
                "card details required");
        assertTrue(thrown.getMessage().contains("required"));

    }


    @Test
    public void shouldSendAtLeastASingleMessage() {
        PaymentMethod paymentMethod= new PaymentMethod("mobilemoney");
        when(paymentMethodRepository.findByMethodNameIgnoreCase(paymentMethod.getMethodName()))
                .thenReturn(Optional.of(paymentMethod));
        Payer payer = new Payer("Me","myself","myself@gmail.com","0718876399");
        Card card = new Card(234,23456789,new Expiry(2022,12));
        Merchant merchant1 = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d","Malek",
                "Walters","m@walters.com","0722897644");
        when(merchantRepository.findByApiKey("ef99a898-a515-414d-9655-5bf1a0fede0d")).thenReturn(Optional.of(merchant1));

        PaymentRequest paymentRequest = new PaymentRequest("niwjenfiwe","KEN","KES", 20,
                paymentMethod.convertPaymentEntityToDTO(),payer.convertPayerEntityToDTO(),card);
        paymentServiceUnderTest.paymentProcessor(paymentRequest,"ef99a898-a515-414d-9655-5bf1a0fede0d");

    }

}
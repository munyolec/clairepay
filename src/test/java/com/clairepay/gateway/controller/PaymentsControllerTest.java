package com.clairepay.gateway.controller;

import com.clairepay.gateway.dto.*;
import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.models.PaymentMethod;
import com.clairepay.gateway.models.Payments;
import com.clairepay.gateway.service.PayerService;
import com.clairepay.gateway.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PaymentsControllerTest {

    @MockBean
    private PaymentService paymentService;
    @MockBean
    private PayerService payerService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void it_should_return_list_of_all_payments() throws Exception{
        Payments payment = new Payments();
        doReturn(Lists.newArrayList(payment)).when(paymentService).getAllPayments();
        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/payments/")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(1)));
    }


    @Test
    void it_should_return_list_of_zero_payments_of_specified_payer() throws Exception{
        Payer payerOne = new Payer("Claire", "Mun", "mun@gmail.com", "0718892704");
        Payments payment = new Payments();
        doReturn(Lists.newArrayList(payment)).when(paymentService).getPayerPayment(payerOne.getPayerId());

        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/payments/1/payments")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(0)));
    }


    @Test
    void it_should_return_response_when_payment_is_made() throws Exception {
        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO("card");
        Payer payer = new Payer("Me","myself","myself@gmail.com","0718876399");
        Card card = new Card(234,23456789,new Expiry(2022,12));
        PaymentRequest paymentRequest = new PaymentRequest("niwjenfiwe","KEN","KES", 20,
                paymentMethodDTO,payer.convertPayerEntityToDTO(),card);
        String apiKey = "ef99a898-a515-414d-9655-5bf1a0fede0d";
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setReferenceId(paymentRequest.getReferenceId());

        Mockito.when(paymentService.paymentProcessor(paymentRequest,apiKey)).thenReturn(paymentResponse);

        String jsonRequest = objectMapper.writeValueAsString(paymentRequest);
        mockMvc.perform(post("/api/v1/clairepay/payments/postPayment").content(jsonRequest)
                        .header("apiKey",apiKey)
                        .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.reference_id").value(paymentRequest.getReferenceId()));


    }
}
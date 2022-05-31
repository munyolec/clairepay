package com.clairepay.gateway.controller;

import com.clairepay.gateway.AbstractTest;
import com.clairepay.gateway.dto.PayerDTO;
import com.clairepay.gateway.models.Payer;
import com.clairepay.gateway.repository.PayerRepository;
import com.clairepay.gateway.service.PayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PayerController.class)
class PayerControllerTest extends AbstractTest {

    @MockBean
    private PayerService payerService;

    @MockBean
    private PayerRepository payerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void getAllPayers() throws Exception {
        Payer payerOne = new Payer("Claire", "Mun", "mun@gmail.com", "0718892704");
        doReturn(Lists.newArrayList(payerOne)).when(payerService).getAllPayers();

        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/payers/")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(1)));
    }

    @Test
    void getPayer() throws Exception{
        Payer payerOne = new Payer("Nekesa", "Mun", "mun@gmail.com", "0718892704");
        doReturn(Optional.of(payerOne)).when(payerService).getPayerByEmail("mun@gmail.com");

        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/payers/{email}","mun@gmail.com")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Nekesa"));

    }

    @Test
    void addNewPayer() throws Exception {
        Payer payer = new Payer("Jam", "Mo","jam@gmail.com","0712786340");
       when(payerService.addNewPayer(ArgumentMatchers.any())).thenReturn(payer);

        String jsonRequest = objectMapper.writeValueAsString(payer);
        mockMvc.perform(post("/api/v1/clairepay/payers/newPayer").content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Jam"));


    }
}
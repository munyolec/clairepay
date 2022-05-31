package com.clairepay.gateway.controller;

import com.clairepay.gateway.AbstractTest;
import com.clairepay.gateway.models.Merchant;
import com.clairepay.gateway.service.MerchantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebAppConfiguration
@WebMvcTest(controllers = MerchantController.class)
class MerchantControllerTest extends AbstractTest {
    private MockMvc mockMvc;

    @MockBean
    private MerchantService merchantService;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    void getAllMerchants() throws Exception{
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d", "Malek", "Waters",
                "malek@gmail.com", "0700256345"
        );
        doReturn(Lists.newArrayList(merchant)).when(merchantService).getMerchantList();

        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/merchants/")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(1)));
    }

    @Test
    void getMerchant() throws Exception{
        Merchant merchant = new Merchant("ef99a898-a515-414d-9655-5bf1a0fede0d", "Malek", "Waters",
                "malek@gmail.com", "0700256345"
        );
        doReturn(Lists.newArrayList(merchant)).when(merchantService).getMerchant(1L);
        mockMvc.perform( MockMvcRequestBuilders.get("/api/v1/clairepay/merchants/1")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(1)));

    }
}
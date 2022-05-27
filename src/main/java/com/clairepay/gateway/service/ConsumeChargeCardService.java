package com.clairepay.gateway.service;

import com.clairepay.gateway.dto.ChargeCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Slf4j
public class ConsumeChargeCardService {
    private final RestTemplate restTemplate;
    @Autowired
    public ConsumeChargeCardService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void callChargeCardAPI(ChargeCard card) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<ChargeCard> entity = new HttpEntity<>(card, headers);
        log.info("Charge Card Response -> " + restTemplate.exchange(
                "http://localhost:8081/api/v1/card/charge", HttpMethod.POST, entity, String.class).getBody());
    }
}

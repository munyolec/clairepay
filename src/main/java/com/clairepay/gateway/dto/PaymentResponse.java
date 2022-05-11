package com.clairepay.gateway.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentResponse {
    private String requestId;
    private String response_code;
    private String response_description;
    private String referenceId;
    private String transaction_id = UUID.randomUUID().toString();
}

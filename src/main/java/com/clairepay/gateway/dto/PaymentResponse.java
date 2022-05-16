package com.clairepay.gateway.dto;

import lombok.*;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String requestId = UUID.randomUUID().toString();
    private String response_code;
    private String response_description;
    private String referenceId;
    private String transaction_id = UUID.randomUUID().toString();
}

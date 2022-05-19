package com.clairepay.gateway.dto;

import com.clairepay.gateway.filter.ThreadLocalRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Data
@ToString
@AllArgsConstructor

@Builder
public class PaymentResponse {
    @JsonProperty("request_id")
    private String requestId ;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_description")
    private String responseDescription;

    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("transaction_id")
    private String transactionId = UUID.randomUUID().toString();

    public PaymentResponse() {
        this.requestId = ThreadLocalRequest.getRequestId();
    }
}

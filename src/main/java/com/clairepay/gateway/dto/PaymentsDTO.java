package com.clairepay.gateway.dto;

import com.clairepay.gateway.models.CardDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentsDTO {
    private Long paymentId;
    private String currency;
    private String amount;
    private String paymentMethod;
    private PayerDTO payer;

    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CardDetails cardDetails;

}



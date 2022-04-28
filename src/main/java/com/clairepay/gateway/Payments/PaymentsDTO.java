package com.clairepay.gateway.Payments;

import com.clairepay.gateway.CardDetails.CardDetails;
import com.clairepay.gateway.Payer.PayerDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.Null;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
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



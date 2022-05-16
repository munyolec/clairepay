package com.clairepay.gateway.dto;

import com.clairepay.gateway.CardDetails.CardDetails;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.Payer.PayerDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest  {
    @JsonProperty("reference_id")
    @NotEmpty(message="reference id is required")
    private String referenceId;

    @NotEmpty(message="country is required")
    @Size(min=3, max=4)
    private String country;

    @NotEmpty(message="currency is required")
    @Size(min=3, max=4)
    private String currency;

    @NotNull(message="amount is required")
    private Integer amount;

    @NotEmpty(message="payment method is required")
    private String paymentMethod;

//    @Valid
    private Payer payer;

    @Valid
    private Card card;

    }

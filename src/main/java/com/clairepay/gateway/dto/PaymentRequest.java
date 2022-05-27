package com.clairepay.gateway.dto;

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
public class PaymentRequest {
    @JsonProperty("reference_id")
    @NotEmpty(message = "reference id is required")
    private String referenceId;

    @NotEmpty(message = "country is required")
    @Size(min = 3, max = 4)
    private String country;

    @NotEmpty(message = "currency is required")
    @Size(min = 3, max = 4)
    private String currency;

    @NotNull(message = "amount is required")
    private Integer amount;

    @Valid
    private PaymentMethodDTO paymentMethod;

    @Valid
    private PayerDTO payer;

    @Valid
    private Card card;


    public PaymentRequest(String referenceId, String country, String currency, Integer amount, PayerDTO payer, Card card) {
        this.referenceId = referenceId;
        this.country = country;
        this.currency = currency;
        this.amount = amount;
        this.payer = payer;
        this.card = card;
    }

    public PaymentRequest(String referenceId, String country, String currency, Integer amount, PaymentMethodDTO paymentMethod, PayerDTO payer) {
        this.referenceId = referenceId;
        this.country = country;
        this.currency = currency;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.payer = payer;
    }
}


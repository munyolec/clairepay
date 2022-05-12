package com.clairepay.gateway.dto;

import com.clairepay.gateway.CardDetails.CardDetails;
import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Payer.Payer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Null;

@Data
public class PaymentRequest {
    @JsonProperty("reference_id")

    private String referenceId;
    private String country;
    private String currency;
    private Integer amount;
    private String paymentMethod;
    private Payer payer;
    private Merchant merchant;
    private CardDetails card;

}
package com.clairepay.gateway.dto;

import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

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
//    @NotEmpty(message="payment is required")
    private PaymentMethodDTO paymentMethod;

    @Valid
    private PayerDTO payer;

    @Valid
    private Card card;

}


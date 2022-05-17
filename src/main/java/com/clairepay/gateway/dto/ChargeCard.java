package com.clairepay.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeCard {
    @NotEmpty(message = "card_holder is required")
    @JsonProperty("card_holder")
    private String cardHolder;

    @Size(min = 3, max = 3, message = "invalid currency")
    @NotEmpty(message = "currency is required")
    private String currency;

    @Size(min = 3, max = 3, message = "invalid country")
    @NotEmpty(message = "country is required")
    private String country;

    @Email(message = "email is invalid")
    @NotEmpty(message = "email is required")
    private String email;

    @NotNull(message = "card_number is required")
    @JsonProperty("card_number")
    private Long cardNumber;

    @NotNull(message = "cvv is required")
    private int cvv;

    @Valid
    @NotNull(message = "expiry object data is required")
    private Expiry expiry;

    @NotNull(message = "amount is required")
    private int amount;

}

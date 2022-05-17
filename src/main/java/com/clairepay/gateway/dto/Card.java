package com.clairepay.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @NotNull(message="cvv is required")
    private int cvv;

    @NotNull(message="card number is required")
    private long cardNumber;

    @Valid
    private Expiry expiry;
}
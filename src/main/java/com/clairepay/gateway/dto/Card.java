package com.clairepay.gateway.dto;

import lombok.Data;

@Data
public class Card {
    private Integer cvv;
    private String cardNumber;
    private Expiry expiry;
}
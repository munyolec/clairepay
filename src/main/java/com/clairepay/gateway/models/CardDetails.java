package com.clairepay.gateway.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table
public class CardDetails {
    @Id
    @SequenceGenerator(name = "cardDetails_sequence",
            sequenceName = "cardDetails_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cardDetails_sequence"
    )
    private String cvv;
    private String cardNumber;
    @NotNull
    private String expiryDate;
    @NotNull

    public CardDetails(){

    }

    public CardDetails(String cardNumber, String expiryDate) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    public CardDetails(String cvv, String cardNumber, String expiryDate) {
        this.cvv = cvv;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }
}

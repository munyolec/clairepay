package com.clairepay.gateway.CardDetails;

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
    public String cvv;
    public String cardNumber;
    @NotNull
    public String expiryDate;
    @NotNull

    public CardDetails(){

    }

    public CardDetails(String cvv, String cardNumber, String expiryDate) {
        this.cvv = cvv;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }

    public CardDetails(String cardNumber, String expiryDate) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
    }
}

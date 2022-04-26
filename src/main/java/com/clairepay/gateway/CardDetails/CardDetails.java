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
    public String cardNumber;
    @NotNull
    public String month;
    @NotNull
    public String year;
    @NotNull
    public String cvv;

    public CardDetails(){

    }
}

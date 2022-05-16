package com.clairepay.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MpesaQueue implements Serializable {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String payment_intent_request_id =  UUID.randomUUID().toString();
    private Integer amount;

    public MpesaQueue(String firstName, String lastName, String phoneNumber, Integer amount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.amount = amount;
    }
}

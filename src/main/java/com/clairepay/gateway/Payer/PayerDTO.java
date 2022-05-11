package com.clairepay.gateway.Payer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PayerDTO {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;


}


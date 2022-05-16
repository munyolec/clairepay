package com.clairepay.gateway.Payer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayerDTO {
    @NotEmpty(message="first name is required")
    private String firstName;

    @NotEmpty(message="first name is required")
    private String lastName;

    @NotEmpty(message="phone number is required")
    private String phoneNumber;

    @NotEmpty(message="email is required")
    @Email
    private String email;


}


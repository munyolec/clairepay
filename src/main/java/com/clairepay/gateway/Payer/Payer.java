package com.clairepay.gateway.Payer;


import com.clairepay.gateway.Payments.Payments;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.Email;

import javax.validation.constraints.Size;

import java.util.List;

@Data
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property="payerId")
public class Payer {
    @Id
    @SequenceGenerator(name = "payer_sequence",
            sequenceName = "payer_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payer_sequence"
    )
    private Long payerId;
    private String firstName;
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Size(min=10)
    private String phoneNumber;


    @OneToMany(mappedBy="payer")
//    @JsonIgnore
    private List<Payments> payments;

    public Payer(){
    }

    public Payer(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        this.phoneNumber = phoneNumber;

    }
    public PayerDTO convertPayerEntityToDTO() {
        PayerDTO payerDTO = new PayerDTO();
        payerDTO.setFirstName(this.getFirstName());
        payerDTO.setLastName(this.getLastName());
        payerDTO.setPhoneNumber(this.getPhoneNumber());
        payerDTO.setEmail(this.getEmail());
        return payerDTO;
    }

}

package com.clairepay.gateway.Payer;


import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.Payments.Payments;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table
public class Payer {
    @Id
    @SequenceGenerator(name = "payer_sequence",
            sequenceName = "payer_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payer_sequence"
    )
    private long payerId;
    private String firstName;
    private String lastName;

    @Email
    @Column(nullable = false, unique = true)
    private String email;
    @Size(min=10)
    private String phoneNumber;


    @OneToMany(mappedBy="payer")
    private Set<Payments> payments;

    public Payer(){

    }

    public Payer(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        this.phoneNumber = phoneNumber;

    }
}

package com.clairepay.gateway.Merchant;

import com.clairepay.gateway.Payer.UniqueEmail;
import com.clairepay.gateway.Payments.Payments;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property="merchantId")

public class Merchant {
    @Id
    @SequenceGenerator(name = "merchant_sequence",
            sequenceName = "merchant_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "merchant_sequence"
    )
    private Long merchantId;

//    private String apiKey;
    private String apiKey = UUID.randomUUID().toString();
    private String firstName;
    private String lastName;
    @NotNull
//    @UniqueEmail
    private String email;
    @Size(min=10)
    private String phoneNumber;
    private Integer merchantBalance;

    @OneToMany(mappedBy="merchant")
    private List<Payments> payments;

    public Merchant() {
    }

    public Merchant(String firstName, String lastName, String email, String phoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.merchantBalance=0;

    }



}

package com.clairepay.gateway.Merchant;

import com.clairepay.gateway.Payments.Payments;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Entity
@Table
public class Merchant {
    @Id
    @SequenceGenerator(name = "merchant_sequence",
            sequenceName = "merchant_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "merchant_sequence"
    )
    private long merchantId;


    private String apiKey;
    private String firstName;
    private String lastName;
    @NotNull
    private String email;
    @Size(min=10)
    private String phoneNumber;
    private Integer merchantBalance;

    @OneToMany(mappedBy="merchant")
    private Set<Payments> payments;

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

package com.clairepay.gateway.models;


import com.clairepay.gateway.validation.UniqueEmail;
import com.clairepay.gateway.dto.PayerDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
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
    @NotEmpty(message="first name required")
    private String firstName;
    @NotEmpty(message="last name required")
    private String lastName;

    @Email
    @UniqueEmail
    @Column(unique = true)
    @NotEmpty(message="email required")
    private String email;
    @Size(min=10)
    @NotNull
    @Pattern(regexp="^\\d{10}$")
    private String phoneNumber;

    @OneToMany(mappedBy="payer",fetch = FetchType.LAZY)
    @ToString.Exclude
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Payer payer = (Payer) o;
        return payerId != null && Objects.equals(payerId, payer.payerId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

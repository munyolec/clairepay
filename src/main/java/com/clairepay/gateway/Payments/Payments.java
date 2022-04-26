package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table
public class Payments {
    @Id
    @SequenceGenerator(name = "payments_sequence",
            sequenceName = "payments_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payments_sequence"
    )
    private long paymentId;

    @ManyToOne
    @JoinColumn(name="payer_Id", nullable=false)
    private Payer payer;

    @ManyToOne
    @JoinColumn(name="merchant_Id", nullable=false)
    private Merchant merchant;
    @ManyToOne
    @JoinColumn(name="method_Id", nullable=false)
    private PaymentMethod paymentMethod;


    private String currency;
    private String amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Payments(){

    }

}

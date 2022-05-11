package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Merchant.Merchant;
import com.clairepay.gateway.Payer.Payer;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property="paymentId")

public class Payments {
    @Id
    @SequenceGenerator(name = "payments_sequence",
            sequenceName = "payments_sequence",
            allocationSize = 1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "payments_sequence"
    )
    private Long paymentId;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "payer_Id", nullable = false)
    private Payer payer;
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "merchant_Id", nullable = false)
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "method_Id", nullable = false)
    private PaymentMethod paymentMethod;

    private String currency = "KES";
    private Integer amount;

    @Enumerated(EnumType.STRING)
    private PaymentsStatus status;
    public Payments() {
    }

    public Payments(Long paymentId, Payer payer, Merchant merchant, PaymentMethod paymentMethod, String currency, Integer amount, PaymentsStatus status) {
        this.paymentId = paymentId;
        this.payer = payer;
        this.merchant = merchant;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        this.amount = amount;
        this.status = status;
    }
}

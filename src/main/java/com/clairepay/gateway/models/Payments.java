package com.clairepay.gateway.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "paymentId")

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payer_Id", nullable = false)
    private Payer payer;
    @ManyToOne()
    @JoinColumn(name = "merchant_Id", nullable = false)
    private Merchant merchant;

    @ManyToOne
    @JoinColumn(name = "method_Id")
    private PaymentMethod paymentMethod;
    private String currency = "KES";
    private Integer amount;
    private String transactionId;

    private String referenceId;
    @Enumerated(EnumType.STRING)
    private PaymentsStatus status;

    public Payments() {
    }

    public Payments(Long paymentId, Payer payer, Merchant merchant, PaymentMethod paymentMethod,
                    String currency, Integer amount, PaymentsStatus status) {
        this.paymentId = paymentId;
        this.payer = payer;
        this.merchant = merchant;
        this.paymentMethod = paymentMethod;
        this.currency = currency;
        this.amount = amount;
        this.status = status;
    }
}

package com.clairepay.gateway.PaymentMethod;

import com.clairepay.gateway.Payments.Payments;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table
public class PaymentMethod {
    @Id
    @SequenceGenerator(name = "paymentMethod_sequence",
            sequenceName = "paymentMethod_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "paymentMethod_sequence"
    )
    private long methodId;
    private String methodName;

    @OneToMany(mappedBy="paymentMethod")
    private Set<Payments> payments;

    public PaymentMethod() {}

    public PaymentMethod(String methodName) {
        this.methodName = methodName;
    }
}

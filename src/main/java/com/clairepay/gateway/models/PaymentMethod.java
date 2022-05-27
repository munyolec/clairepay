package com.clairepay.gateway.models;

import com.clairepay.gateway.dto.PayerDTO;
import com.clairepay.gateway.dto.PaymentMethodDTO;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,property="methodId")

public class PaymentMethod {
    @Id
    @SequenceGenerator(name = "paymentMethod_sequence",
            sequenceName = "paymentMethod_sequence",
            allocationSize =1)
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "paymentMethod_sequence"
    )
    private Long methodId;
    private String methodName;

    @OneToMany(mappedBy="paymentMethod")
    private List<Payments> payments;

    public PaymentMethod() {}

    public PaymentMethod(String methodName) {
        this.methodName = methodName;
    }

    public PaymentMethodDTO convertPaymentEntityToDTO() {
        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO();
        paymentMethodDTO.setMethodName(this.getMethodName());
        return paymentMethodDTO;
    }

}

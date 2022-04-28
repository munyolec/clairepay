package com.clairepay.gateway.Payments;

import com.clairepay.gateway.Payer.PayerDTO;
import com.clairepay.gateway.PaymentMethod.PaymentMethod;
import com.clairepay.gateway.PaymentMethod.PaymentMethodDTO;
import lombok.Data;

@Data
public class PaymentsDTO {
    private Long paymentId;
    private String currency;
    private String amount;
    private PaymentMethodDTO paymentMethod;
    private PayerDTO payer;

}



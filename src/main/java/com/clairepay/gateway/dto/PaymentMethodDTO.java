package com.clairepay.gateway.dto;

import lombok.Data;

@Data
public class PaymentMethodDTO {
    private Long methodId;
    private String methodName;
}

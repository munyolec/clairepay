package com.clairepay.gateway.dto;

import lombok.Data;

@Data
public class MerchantDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Integer merchantBalance;
}

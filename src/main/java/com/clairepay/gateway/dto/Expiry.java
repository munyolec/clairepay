package com.clairepay.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expiry {
    @NotNull(message="month is required")
    private Integer month;

    @NotNull(message="year is required")
    private Integer year;
}
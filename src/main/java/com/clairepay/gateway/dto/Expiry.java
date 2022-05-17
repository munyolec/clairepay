package com.clairepay.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expiry {
    @NotNull(message="month is required")
    @Range(min= 1, max= 12, message="month invalid")
    private int month;

    @NotNull(message="year is required")
    private int year;
}
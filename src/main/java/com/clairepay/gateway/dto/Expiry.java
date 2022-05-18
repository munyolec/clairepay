package com.clairepay.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expiry {
    @NotNull(message="year is required")
    private Integer year;

    @NotNull(message="month is required")
    @Range(min= 1, max= 12, message="month invalid")
    private Integer month;

}
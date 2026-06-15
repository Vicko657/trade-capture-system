package com.technicalchallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashflowDTO {
    private Long id;
    private Long legId;
    @Positive(message = "Cashflow value must be positive")
    private BigDecimal paymentValue;
    @NotNull(message = "Value date is required")
    private LocalDate valueDate;
    private Double rate;
    private String payRec;
    private String paymentType;
    private String paymentBusinessDayConvention;
    private LocalDateTime createdDate;
    private Boolean active;
}

package com.technicalchallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashflowDTO {

    @Schema(description = "Internal database identifier of the cashflow record", example = "6000")
    private Long id;

    @Schema(description = "Id of the tradeleg the this cashflow belongs to", example = "200")
    private Long legId;

    @Schema(description = "Monetary value of cashflow value", example = "87500.00")
    @Positive(message = "Cashflow value must be positive")
    private BigDecimal paymentValue;

    @Schema(description = "Date on which the cashflow is paid", example = "2025-09-04")
    @NotNull(message = "Value date is required")
    private LocalDate valueDate;

    @Schema(description = "Rate used to calculate this cashflow", example = "0.035")
    private Double rate;

    @Schema(description = "Whether the cashflow is paid or recieved", example = "PAY")
    private String payRec;

    @Schema(description = "Type of payment: fixed or floating", example = "FIXED")
    private String paymentType;

    @Schema(description = "Business day convention applied to this cashflow's value date", example = "MODFOLLOWING")
    private String paymentBusinessDayConvention;

    @Schema(description = "Timestamp when this cashflow record was created", example = "2025-06-04T14:32:00")
    private LocalDateTime createdDate;

    @Schema(description = "Whether this cashflow is currently active", example = "true")
    private Boolean active;
}

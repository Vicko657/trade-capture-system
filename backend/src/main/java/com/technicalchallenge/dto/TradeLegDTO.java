package com.technicalchallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeLegDTO {

    @Schema(description = "Internal database identifier of the tradeleg record", example = "200")
    private Long legId;

    @Schema(description = "Leg's notional", example = "200000.00")
    @NotNull(message = "Notional is required")
    @Positive(message = "Notional must be positive")
    private BigDecimal notional;

    @Schema(description = "Fixed rate for the leg", example = "0.035")
    private Double rate;

    // Currency reference
    @Schema(description = "Id of the currency for this leg", example = "1")
    private Long currencyId;

    @Schema(description = "Currency of this leg", example = "USD")
    @NotNull(message = "Currency is required")
    private String currency;

    // Leg type reference
    @Schema(description = "Id of the leg type", example = "1")
    private Long legTypeId;

    @Schema(description = "Type of leg: fixed or floating", example = "FIXED")
    @NotNull(message = "legType is required")
    private String legType;

    // Index reference (for floating legs)
    @Schema(description = "Id of the floating rate index", example = "25")
    private Long indexId;

    @Schema(description = "Name of the floating rate index", example = "SOFR")
    @JsonProperty("index")
    private String indexName;

    // Holiday calendar reference
    @Schema(description = "Id of the holiday calendar used for this leg", example = "1")
    private Long holidayCalendarId;

    @Schema(description = "Holiday calendar used to adjust payment dates for this leg", example = "USD")
    @NotNull(message = "HolidayCalendar is required")
    private String holidayCalendar;

    // Schedule reference
    @Schema(description = "Id of the calculation period schedule", example = "2")
    private Long scheduleId;

    @Schema(description = "Frequency at which calculation periods occur for this leg", example = "QUARTERLY")
    @NotNull(message = "Calculation Period Schedule is required")
    private String calculationPeriodSchedule;

    // Business day convention references
    @Schema(description = "Id of the payment business day convention", example = "200")
    private Long paymentBdcId;

    @Schema(description = "Business day convention used to adjust payment dates", example = "MODFOLLOWING")
    @NotNull(message = "PaymentBusinessDayConvention is required")
    private String paymentBusinessDayConvention;

    @Schema(description = "Id of the fixing business day convention", example = "200")
    private Long fixingBdcId;

    @Schema(description = "Business day convention usde to adjust fixing dates", example = "MODFOLLOWING")
    @NotNull(message = "FixingBusinessDayConvention is required")
    private String fixingBusinessDayConvention;

    // Pay/Receive reference
    @Schema(description = "Id of the pay/recieve flag", example = "2")
    private Long payRecId;

    @Schema(description = "Whether this leg is paid or recieved", example = "PAY")
    @NotNull(message = "PayRecFlag is required")
    private String payReceiveFlag;

    // Associated cashflows
    @Schema(description = "List of cashflows generated for this leg")
    private List<CashflowDTO> cashflows;
}

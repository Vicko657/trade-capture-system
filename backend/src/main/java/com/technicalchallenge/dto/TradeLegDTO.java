package com.technicalchallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long legId;

    @NotNull(message = "Notional is required")
    @Positive(message = "Notional must be positive")
    private BigDecimal notional;

    private Double rate;

    // Currency reference
    @NotNull(message = "Currency id is required")
    private Long currencyId;
    @NotNull(message = "Currency is required")
    private String currency;

    // Leg type reference
    @NotNull(message = "legType id is required")
    private Long legTypeId;
    @NotNull(message = "legType is required")
    private String legType;

    // Index reference (for floating legs)
    private Long indexId;
    @JsonProperty("index")
    private String indexName;

    // Holiday calendar reference
    @NotNull(message = "HolidayCalendar id is required")
    private Long holidayCalendarId;
    @NotNull(message = "HolidayCalendar is required")
    private String holidayCalendar;

    // Schedule reference
    @NotNull(message = "Schedule id is required")
    private Long scheduleId;
    @NotNull(message = "Calculation Period Schedule is required")
    private String calculationPeriodSchedule;

    // Business day convention references
    @NotNull(message = "PaymentBusinessDayConvention id is required")
    private Long paymentBdcId;
    @NotNull(message = "PaymentBusinessDayConvention is required")
    private String paymentBusinessDayConvention;
    @NotNull(message = "FixingBusinessDayConvention id is required")
    private Long fixingBdcId;
    @NotNull(message = "FixingBusinessDayConvention is required")
    private String fixingBusinessDayConvention;

    // Pay/Receive reference
    @NotNull(message = "PayRec id is required")
    private Long payRecId;
    @NotNull(message = "PayRecFlag is required")
    private String payReceiveFlag;

    // Associated cashflows
    private List<CashflowDTO> cashflows;
}

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
    private Long currencyId;
    @NotNull(message = "Currency is required")
    private String currency;

    // Leg type reference

    private Long legTypeId;
    @NotNull(message = "legType is required")
    private String legType;

    // Index reference (for floating legs)
    private Long indexId;
    @JsonProperty("index")
    private String indexName;

    // Holiday calendar reference
    private Long holidayCalendarId;
    @NotNull(message = "HolidayCalendar is required")
    private String holidayCalendar;

    // Schedule reference
    private Long scheduleId;
    @NotNull(message = "Calculation Period Schedule is required")
    private String calculationPeriodSchedule;

    // Business day convention references
    private Long paymentBdcId;
    @NotNull(message = "PaymentBusinessDayConvention is required")
    private String paymentBusinessDayConvention;
    private Long fixingBdcId;
    @NotNull(message = "FixingBusinessDayConvention is required")
    private String fixingBusinessDayConvention;

    // Pay/Receive reference
    private Long payRecId;
    @NotNull(message = "PayRecFlag is required")
    private String payReceiveFlag;

    // Associated cashflows
    private List<CashflowDTO> cashflows;
}

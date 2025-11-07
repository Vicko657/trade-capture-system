package com.technicalchallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TradeDTO {
    private Long id;

    private Long tradeId;

    private Integer version;

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    @JsonProperty("startDate")
    @NotNull(message = "Trade start date is required")
    private LocalDate tradeStartDate;

    @JsonProperty("maturityDate")
    @NotNull(message = "Trade maturity date is required")
    private LocalDate tradeMaturityDate;

    @JsonProperty("executionDate")
    @NotNull(message = "Trade execution date is required")
    private LocalDate tradeExecutionDate;

    private String utiCode;
    private LocalDateTime lastTouchTimestamp;
    private LocalDate validityStartDate;
    private LocalDate validityEndDate;
    private Boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime deactivatedDate;

    // Book reference
    @NotNull(message = "Book id is required")
    private Long bookId;
    @NotNull(message = "Book name is required")
    private String bookName;

    // Counterparty reference
    @NotNull(message = "Counterparty id is required")
    private Long counterpartyId;
    @NotNull(message = "Counterparty name is required")
    private String counterpartyName;

    // User references
    @NotNull(message = "TraderUser id is required")
    private Long traderUserId;
    @NotNull(message = "TraderUser name is required")
    private String traderUserName;
    @NotNull(message = "InputterUser id is required")
    private Long tradeInputterUserId;
    @NotNull(message = "InputterUser name is required")
    private String inputterUserName;

    // Trade type references
    @NotNull(message = "Tradetype id is required")
    private Long tradeTypeId;
    @NotNull(message = "Tradetype is required")
    private String tradeType;
    @NotNull(message = "TradeSubtype id is required")
    private Long tradeSubTypeId;
    @NotNull(message = "TradeSubtype is required")
    private String tradeSubType;

    // Trade status
    private Long tradeStatusId;
    private String tradeStatus;

    // Trade legs
    @NotNull(message = "Tradelegs are required")
    @Size(min = 2, max = 2, message = "Trade must have exactly 2 tradelegs")
    private List<TradeLegDTO> tradeLegs;

    // Additional fields for extensibility
    private List<AdditionalInfoDTO> additionalFields;
}

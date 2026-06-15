package com.technicalchallenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Internal database identifier of the trade record", example = "1000")
    private Long id;

    @Schema(description = "Business facing trade identifier", example = "100001")
    private Long tradeId;

    @Schema(description = "Version number of the trade, incremented on each amendment", example = "1")
    private Integer version;

    @Schema(description = "Date the trade was booked", example = "2025-06-04")
    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    @Schema(description = "Date the trade will start", example = "2025-06-04")
    @JsonProperty("startDate")
    @NotNull(message = "Trade start date is required")
    private LocalDate tradeStartDate;

    @Schema(description = "Date the trade will expire", example = "2029-08-25")
    @JsonProperty("maturityDate")
    @NotNull(message = "Trade maturity date is required")
    private LocalDate tradeMaturityDate;

    @Schema(description = "Date the trade is executed", example = "2025-06-08")
    @JsonProperty("executionDate")
    @NotNull(message = "Trade execution date is required")
    private LocalDate tradeExecutionDate;

    @Schema(description = "Unique Trade Identifer for regulatory reporting", example = "AGB5371HSTYW628392HS100001")
    private String utiCode;

    @Schema(description = "Timestamp of the last update made to this trade", example = "2025-06-08T14:32:00")
    private LocalDateTime lastTouchTimestamp;

    @Schema(description = "Date from which this version of the trade is considered valid", example = "2025-06-04")
    private LocalDate validityStartDate;

    @Schema(description = "Date until which this version of the trade is considered valid", example = "2029-08-25")
    private LocalDate validityEndDate;

    @Schema(description = "Whether this trade is currently active", example = "true")
    private Boolean active;

    @Schema(description = "Timestamp when the trade record was created", example = "2025-06-04T14:32:00")
    private LocalDateTime createdDate;

    @Schema(description = "Timestamp when the trade record was deactivated", example = "Trader's Portfoilo Summary")
    private LocalDateTime deactivatedDate;

    // Book reference
    @Schema(description = "Id of the book this trade belongs to", example = "8")
    private Long bookId;

    @Schema(description = "Name of the book this trade belongs to", example = "FX-BOOK-1")
    @NotNull(message = "Book name is required")
    private String bookName;

    // Counterparty reference
    @Schema(description = "Id of the counterparty this trade belongs to", example = "3")
    private Long counterpartyId;

    @Schema(description = "Name of the counterparty this trade belongs to", example = "MegaFund")
    @NotNull(message = "Counterparty name is required")
    private String counterpartyName;

    // User references
    @Schema(description = "Id of the trader who owns the trade", example = "6")
    private Long traderUserId;

    @Schema(description = "Name of the trader who owns the trade", example = "John")
    @NotNull(message = "TraderUser name is required")
    private String traderUserName;

    @Schema(description = "Id of the user who input this trade", example = "4")
    private Long tradeInputterUserId;

    @Schema(description = "Name of the user who input this trade", example = "Bob")
    @NotNull(message = "InputterUser name is required")
    private String inputterUserName;

    // Trade type references
    @Schema(description = "Id of the trade type", example = "1")
    private Long tradeTypeId;

    @Schema(description = "Type of the trade", example = "Swap")
    @NotNull(message = "Tradetype is required")
    private String tradeType;

    @Schema(description = "Id of the trade sub-type", example = "5")
    private Long tradeSubTypeId;

    @Schema(description = "Sub-type of the trade", example = "IR Swap")
    @NotNull(message = "TradeSubtype is required")
    private String tradeSubType;

    // Trade status
    @Schema(description = "Id of the trade status", example = "6")
    private Long tradeStatusId;

    @Schema(description = "Current status of the trade", example = "LIVE")
    private String tradeStatus;

    // Trade legs
    @Schema(description = "The two legs of the trade - one pay leg and one recieve leg")
    @NotNull(message = "Tradelegs are required")
    @Size(min = 2, max = 2, message = "Trade must have exactly 2 tradelegs")
    private List<TradeLegDTO> tradeLegs;

    // Additional fields for extensibility
    @Schema(description = "Additional key-value information attached to this trade, e.g settlement instructions")
    private List<AdditionalInfoDTO> additionalFields;
}

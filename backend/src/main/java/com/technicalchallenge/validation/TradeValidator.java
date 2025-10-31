package com.technicalchallenge.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.service.ApplicationUserService;
import com.technicalchallenge.service.BookService;
import com.technicalchallenge.service.CounterpartyService;
import com.technicalchallenge.service.TradeStatusService;
import com.technicalchallenge.service.TradeTypeService;

@Component
public class TradeValidator {

    private static final Logger logger = LoggerFactory.getLogger(TradeValidator.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private ApplicationUserService applicationUserService;
    @Autowired
    private TradeStatusService tradeStatusService;
    @Autowired
    private TradeTypeService tradeTypeService;

    // Validation for Trade Business Rules
    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {

        List<String> errors = new ArrayList<>();

        // Entity Status Validation - All Reference Data must exist and be valid
        validateAllReferenceData(tradeDTO);

        // Date Validation Rules

        LocalDate maturityDate = tradeDTO.getTradeMaturityDate();
        LocalDate startDate = tradeDTO.getTradeStartDate();
        LocalDate tradeDate = tradeDTO.getTradeDate();
        LocalDate currentDate = LocalDate.now();
        LocalDate executionDate = tradeDTO.getTradeExecutionDate();

        // Trade Business rule - Maturity date cannot be before start date or trade date
        if (maturityDate.isBefore(startDate)) {
            errors.add("Maturity date cannot be before start date");

        } else if (maturityDate.isBefore(tradeDate)) {
            errors.add("Maturity date cannot be before trade date");
        }

        // Trade Business rule - Start date cannot be before trade date
        if (startDate.isBefore(tradeDate)) {
            errors.add("Start date cannot be before trade date");
        }

        // Trade Business rule - Trade date cannot be more than 30 days in the past
        if (tradeDate.minusDays(30).isAfter(currentDate)) {
            errors.add("Trade date cannot be more than 30 days in the past");
        }

        // Trade Business rule - Execution date must be equal to trade date
        if (!executionDate.equals(tradeDate)) {
            errors.add("Execution date must be equal to trade date");
        }

        return ValidationResult.isNotValid(errors);

    }

    // Entity Status Validation
    private void validateAllReferenceData(TradeDTO tradeDTO) {

        // User, book and counterparty must be active, exist and be valid

        // Book Reference Data
        validateBookReference(tradeDTO);

        // Counterparty Reference Data
        validateCounterpartyReference(tradeDTO);

        // User Reference Data
        validateTraderUserReference(tradeDTO);
        validateInputterUserReference(tradeDTO);

        // All reference data must exist and be valid

        // Trade Status Reference
        validateTradeStatusReference(tradeDTO);

        // Trade Type & Sub Type Reference Data
        validateTradeTypeReference(tradeDTO);

        logger.debug("Reference data validation passed for trade");

    }

    private void validateBookReference(TradeDTO tradeDTO) {

        Long bookId = tradeDTO.getBookId();
        String bookName = tradeDTO.getBookName();

        bookService.validateBook(bookId, bookName);

    }

    private void validateCounterpartyReference(TradeDTO tradeDTO) {

        Long counterpartyId = tradeDTO.getCounterpartyId();
        String counterpartyName = tradeDTO.getCounterpartyName();

        counterpartyService.validateCounterparty(counterpartyId, counterpartyName);
    }

    private void validateTraderUserReference(TradeDTO tradeDTO) {

        Long userId = tradeDTO.getTraderUserId();
        String userName = tradeDTO.getTraderUserName();

        applicationUserService.validateUser(userId, userName);

    }

    private void validateInputterUserReference(TradeDTO tradeDTO) {

        Long userId = tradeDTO.getTradeInputterUserId();
        String userName = tradeDTO.getInputterUserName();

        applicationUserService.validateUser(userId, userName);

    }

    private void validateTradeStatusReference(TradeDTO tradeDTO) {

        Long tradeStatusId = tradeDTO.getTradeStatusId();
        tradeStatusService.validateTradeStatus(tradeStatusId);

    }

    private void validateTradeTypeReference(TradeDTO tradeDTO) {

        Long tradeTypeId = tradeDTO.getTradeTypeId();
        String tradeType = tradeDTO.getTradeSubType();
        Long tradeSubTypeId = tradeDTO.getTradeSubTypeId();
        String tradeSubType = tradeDTO.getTradeSubType();

        tradeTypeService.validateTradeType(tradeTypeId, tradeType);
        tradeTypeService.validateTradeSubType(tradeSubTypeId, tradeSubType);

    }

    // Validation for TradeLeg Consisitency Business Rules
    public ValidationResult validateTradeLegConsistency(List<TradeLegDTO> legs) {

        List<String> errors = new ArrayList<>();

        // Trade legs (1st & 2nd)
        TradeLegDTO leg1 = legs.get(0);
        TradeLegDTO leg2 = legs.get(1);

        String legType;
        Long indexId;
        String indexName;
        Double rate;

        // Cross-Leg Business Rules

        // TradeLeg Business rule - Legs must have opposite pay/receive flags
        if (leg1.getPayRecId() == leg2.getPayRecId()) {
            errors.add("Legs must have opposite pay/receive flags");

        } else if (leg1.getPayReceiveFlag() == leg2.getPayReceiveFlag()) {
            errors.add("Legs must have opposite pay/receive flags");
        }

        // TradeLeg Business rule - Floating legs must have an index specified and Fixed
        // legs must have a valid rate
        for (TradeLegDTO tradeleg : legs) {

            legType = tradeleg.getLegType();
            indexId = tradeleg.getIndexId();
            indexName = tradeleg.getIndexName();
            rate = tradeleg.getRate();

            if (legType.equalsIgnoreCase("floating")) {
                if (indexId == null && indexName == null) {
                    errors.add("Floating legs must have an index specified");
                }
            } else if (legType.equalsIgnoreCase("fixed")) {
                if (rate > 0) {
                    errors.add("Fixed legs must have a valid rate");
                }
            }

        }
        return ValidationResult.isNotValid(errors);

    }
}

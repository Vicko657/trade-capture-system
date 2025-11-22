package com.technicalchallenge.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;

/**
 * Trade Validator
 * 
 * <p>
 * Validates the business rules for trades and for the tradelegs.
 * 
 * Reference data is called here too to make sure that the data does exist
 * 
 * Using {@link ValidationResult} and called in {@link GlobalExceptionHandler}
 * </p>
 * 
 * FUTURE: Would create different validators, for entities etc... that will
 * handle validations or create an interface which can abstract the same
 * principles
 * 
 */
@Component
public class TradeValidator {

    private static final Logger logger = LoggerFactory.getLogger(TradeValidator.class);

    /**
     * 
     * 1. Validation for Trade Business Rules
     * 
     */
    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {

        List<String> errors = new ArrayList<>();

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

    /**
     * 
     * 2. Validation for TradeLeg Consisitency Business Rules
     * 
     */
    public ValidationResult validateTradeLegConsistency(List<TradeLegDTO> legs) {

        List<String> errors = new ArrayList<>();

        // Trade legs (1st & 2nd)
        TradeLegDTO leg1 = legs.get(0);
        TradeLegDTO leg2 = legs.get(1);

        String legType;
        Long indexId;
        String indexName;
        Double rate;

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

        // TradeLeg Business rule - Legs must have opposite pay/receive flags
        if (leg1.getPayRecId() == leg2.getPayRecId()) {
            errors.add("Legs must have opposite pay/receive flags");

        } else if (leg1.getPayReceiveFlag() == leg2.getPayReceiveFlag()) {
            errors.add("Legs must have opposite pay/receive flags");
        }

        return ValidationResult.isNotValid(errors);

    }

}

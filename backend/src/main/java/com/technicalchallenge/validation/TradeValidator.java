package com.technicalchallenge.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.TradeDTO;

@Component
public class TradeValidator {

    // Validation for Trade Business Rules
    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {

        List<String> errors = new ArrayList<>();

        // Date Validation Rules

        // Trade Business rule - Maturity date cannot be before start date or trade date
        if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeStartDate())) {
            errors.add("Maturity date cannot be before start date");

        } else if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
            errors.add("Maturity date cannot be before trade date");
        }

        // Trade Business rule - Start date cannot be before trade date
        if (tradeDTO.getTradeStartDate().isBefore(tradeDTO.getTradeDate())) {
            errors.add("Start date cannot be before trade date");
        }

        // Trade Business rule - Trade date cannot be more than 30 days in the past
        if (tradeDTO.getTradeStartDate().minusDays(30).isAfter(LocalDate.now())) {
            errors.add("Trade date cannot be more than 30 days in the past");

        }

        return ValidationResult.isNotValid(errors);

    }

}

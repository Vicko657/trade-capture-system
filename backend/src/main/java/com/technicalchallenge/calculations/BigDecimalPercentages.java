package com.technicalchallenge.calculations;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

/**
 * BigDecimalPercentages class provides different calculations using BigDecimals
 * to use for Cashflow Generation and Dashboard Views.
 *
 */
@Component
public class BigDecimalPercentages {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    // Converts the rate to a decimal
    public BigDecimal percentToDecimal(BigDecimal rate) {
        return rate.divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
    }

    // Calculates the percentage paymentValue
    public BigDecimal toPercentageOf(BigDecimal notional, BigDecimal rate, BigDecimal months) {
        return notional.multiply(rate).multiply(months).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    // Calculates the percentage of metrics
    public BigDecimal toPercentageOf(BigDecimal totalNotional, BigDecimal difference) {

        return totalNotional.compareTo(BigDecimal.ZERO) != 0
                ? difference.divide(totalNotional, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.ZERO)
                : BigDecimal.ZERO;

    }

}

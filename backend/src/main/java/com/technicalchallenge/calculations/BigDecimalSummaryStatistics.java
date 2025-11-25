package com.technicalchallenge.calculations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.stream.Collector;

import org.springframework.stereotype.Component;

/**
 * BigDecimalSummaryStatistics class provides different calculations using
 * BigDecimals to use for DashBoardViews.
 *
 */
@Component
public class BigDecimalSummaryStatistics {

    public static Collector<BigDecimal, BigDecimalSummaryStatistics, BigDecimalSummaryStatistics> statistics() {
        return Collector.of(BigDecimalSummaryStatistics::new,
                BigDecimalSummaryStatistics::accept, BigDecimalSummaryStatistics::merge);
    }

    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal min = null;
    private BigDecimal max = null;
    private long count = 0;

    public void accept(BigDecimal value) {

        if (value == null)
            return;

        if (count == 0) {
            sum = value;
            min = value;
            max = value;
            count = 1;
        } else {
            sum = sum.add(value);
            min = min.min(value);
            max = max.max(value);
            count++;
        }
    }

    public BigDecimalSummaryStatistics merge(BigDecimalSummaryStatistics statistics) {

        if (statistics.count == 0)
            return this;

        if (this.count == 0) {
            this.count = statistics.count;
            this.sum = statistics.sum;
            this.min = statistics.min;
            this.max = statistics.max;
        } else {
            this.sum = sum.add(statistics.sum);
            this.min = statistics.min;
            this.max = statistics.max;
            this.count += statistics.count;
        }

        return this;
    }

    public long getCount() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAverage(MathContext mc) {
        if (count == 0)
            return BigDecimal.ZERO;
        return sum.divide(BigDecimal.valueOf(count), mc);
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    @Override
    public String toString() {
        return count == 0 ? "BigSummaryStatistics: Empty"
                : (count + " elements between " + min + " and " + max + ", sum=" + sum);
    }

}

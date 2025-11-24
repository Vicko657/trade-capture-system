# Comprehensive Technical Investigation Report

### Cashflow Bug Investigation: calculateCashflowValue method

#### Executive Summary:

The calculateCashflowValue method in the CashflowService (moved from TradeService) is producing incorrect and inconsistent cashflow values when generated for tradelegs.

#### Technical Investigation:

calculateCashflowValue method:

```
private BigDecimal calculateCashflowValue(TradeLeg leg, int monthsInterval)
```

Investigation Findings:

1. Wrong data type for Notional

```
double notional = leg.getNotional().doubleValue();
```

2. Rate is not converted to a decimal and Rate and Month are Doubles

```
double rate = leg.getRate();
double months = monthsInterval;
```

3. The percentage handling for BigDecimal calculations is incorrect

```
double result = (notional * rate * months) / 12;
```

Other findings:

4. Missing Schedule Strings need to be added in the database

```
            case "monthly":
                return 1;
            case "quarterly":
                return 3;
            case "semi-annually":
            case "semiannually":
            case "half-yearly":
                return 6;
            case "annually":
            case "yearly":
                return 12;
```

#### Root Cause:

- The tradelegs notional is a BigDecimal and has been converted into a Double on line 163, which can cause floating-point precision errors and isn't ideal for financial calculations.

- Rate is a double and needs to be converted into a decimal to prevent inflation of cashflows. Using the rate percentage, without converting can cause incorrect financial calculations.

- The monthInterval is a int and is converted into a double but should be converted to a BigDecimal.

- The result value for the percentage calculation is a double, which can cause rounding errors. A BigDecimal format needs to be used for financial rounding, which will allow precise control over scale and rounding. It is essential for a trading applications as the accuracy is crucial.

- The Schedule values need to be normalised, as they are is searched in the database before used in the parseSchedule method, to get the months interval.

#### Proposed Solution:

- Convert the all the values to Big Decimals
- Create a BigDecimal calculations method to calculate the percentages.
- Make sure the cashflow mapping is correct, i.e Set the paymentType when the Cashflow is created
- Add more schedule values into the database to test different schedules.
- Create Unit and Integration testing to validate the cashflows are working.

Alternatively use a plugin to calculate BigDecimals:

- BigDecimalSummaryStatistics: https://eclipse.dev/collections/javadoc/8.1.0/org/eclipse/collections/impl/collector/BigDecimalSummaryStatistics.html

- BigDecimalMath: https://github.com/eobermuhlner/big-math

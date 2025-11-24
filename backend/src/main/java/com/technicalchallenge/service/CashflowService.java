package com.technicalchallenge.service;

import com.technicalchallenge.calculations.BigDecimalPercentages;
import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.repository.CashflowRepository;
import com.technicalchallenge.repository.BusinessDayConventionRepository;
import com.technicalchallenge.repository.LegTypeRepository;
import com.technicalchallenge.repository.PayRecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CashflowService {
    private static final Logger logger = LoggerFactory.getLogger(CashflowService.class);
    @Autowired
    private CashflowRepository cashflowRepository;
    @Autowired
    private PayRecRepository payRecRepository;
    @Autowired
    private LegTypeRepository legTypeRepository;
    @Autowired
    private BusinessDayConventionRepository businessDayConventionRepository;
    @Autowired
    private BigDecimalPercentages bigDecimalPercentages;

    public List<Cashflow> getAllCashflows() {
        logger.info("Retrieving all cashflows");
        return cashflowRepository.findAll();
    }

    public Optional<Cashflow> getCashflowById(Long id) {
        logger.debug("Retrieving cashflow by id: {}", id);
        return cashflowRepository.findById(id);
    }

    public Cashflow saveCashflow(Cashflow cashflow) {
        logger.info("Saving cashflow: {}", cashflow);
        // Business logic: value must be positive, valueDate required (enforced in
        // controller)
        if (cashflow.getPaymentValue() == null
                || cashflow.getPaymentValue().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Cashflow value must be greater than 0");
        }
        if (cashflow.getValueDate() == null) {
            throw new IllegalArgumentException("Cashflow valueDate is required");
        }
        // Ensure Cashflow is saved with related entities set, not just IDs
        return cashflowRepository.save(cashflow);
    }

    public void deleteCashflow(Long id) {
        logger.warn("Deleting cashflow with id: {}", id);
        cashflowRepository.deleteById(id);
    }

    /**
     * FIXED: Generate cashflows based on schedule and maturity date
     */
    public List<Cashflow> generateCashflows(TradeLeg leg, LocalDate startDate, LocalDate maturityDate) {
        logger.info("Generating cashflows for leg {} from {} to {}", leg.getLegId(), startDate, maturityDate);

        List<Cashflow> cashflows = new ArrayList<>();

        // Use default schedule if not set
        String schedule = "3M"; // Default to quarterly
        if (leg.getCalculationPeriodSchedule() != null) {
            schedule = leg.getCalculationPeriodSchedule().getSchedule();
        }

        int monthsInterval = parseSchedule(schedule);
        List<LocalDate> paymentDates = calculatePaymentDates(startDate, maturityDate, monthsInterval);

        for (LocalDate paymentDate : paymentDates) {

            Cashflow cashflow = new Cashflow();
            cashflow.setTradeLeg(leg); // Fixed field name
            cashflow.setValueDate(paymentDate);
            cashflow.setRate(leg.getRate());

            // Calculate value based on leg type
            BigDecimal cashflowValue = calculateCashflowValue(leg, monthsInterval);
            cashflow.setPaymentValue(cashflowValue);
            cashflow.setPayRec(leg.getPayReceiveFlag());
            cashflow.setPaymentType(leg.getLegRateType());
            cashflow.setPaymentBusinessDayConvention(leg.getPaymentBusinessDayConvention());
            cashflow.setCreatedDate(LocalDateTime.now());
            cashflow.setActive(true);

            Cashflow savedCashflow = cashflowRepository.save(cashflow);

            // Fixed: Makes sure the Tradeleg contains the cashflows once they are saved.
            cashflows.add(savedCashflow);

        }

        logger.info("Generated {} cashflows for leg {}", paymentDates.size(), leg.getLegId());
        return cashflows;

    }

    private int parseSchedule(String schedule) {
        if (schedule == null || schedule.trim().isEmpty()) {
            return 3; // Default to quarterly
        }

        schedule = schedule.trim();

        // Handle common schedule names
        switch (schedule.toLowerCase()) {
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
            default:
                // Parse "1M", "3M", "12M" format
                if (schedule.endsWith("M") || schedule.endsWith("m")) {
                    try {
                        return Integer.parseInt(schedule.substring(0, schedule.length() - 1));
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Invalid schedule format: " + schedule);
                    }
                }
                throw new RuntimeException("Invalid schedule format: " + schedule
                        + ". Supported formats: Monthly, Quarterly, Semi-annually, Annually, or 1M, 3M, 6M, 12M");
        }
    }

    private List<LocalDate> calculatePaymentDates(LocalDate startDate, LocalDate maturityDate, int monthsInterval) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate.plusMonths(monthsInterval);

        while (!currentDate.isAfter(maturityDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusMonths(monthsInterval);
        }

        return dates;
    }

    private BigDecimal calculateCashflowValue(TradeLeg leg, int monthsInterval) {
        if (leg.getLegRateType() == null) {
            return BigDecimal.ZERO;
        }

        String legType = leg.getLegRateType().getType();

        if ("Fixed".equals(legType)) {

            // Notional kept as a BigDecimal
            BigDecimal notional = leg.getNotional();

            // Converts the rate double to BigDecimal decimal
            Double rate = leg.getRate();
            BigDecimal rateDecimal = bigDecimalPercentages.percentToDecimal(BigDecimal.valueOf(rate));

            // Converts the month int to BigDecimal
            BigDecimal months = BigDecimal.valueOf(monthsInterval);

            // Percentage Calculation for a fixed leg
            BigDecimal result = bigDecimalPercentages.toPercentageOf(notional, rateDecimal, months);

            return result;

        } else if ("Floating".equals(legType)) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.ZERO;
    }

    public void populateReferenceDataByName(Cashflow cashflow, CashflowDTO dto) {
        if (dto.getPayRec() != null) {
            cashflow.setPayRec(payRecRepository.findAll().stream()
                    .filter(p -> p.getPayRec().equalsIgnoreCase(dto.getPayRec()))
                    .findFirst().orElse(null));
        }
        if (dto.getPaymentType() != null) {
            cashflow.setPaymentType(legTypeRepository.findAll().stream()
                    .filter(l -> l.getType().equalsIgnoreCase(dto.getPaymentType()))
                    .findFirst().orElse(null));
        }
        if (dto.getPaymentBusinessDayConvention() != null) {
            cashflow.setPaymentBusinessDayConvention(businessDayConventionRepository.findAll().stream()
                    .filter(b -> b.getBdc().equalsIgnoreCase(dto.getPaymentBusinessDayConvention()))
                    .findFirst().orElse(null));
        }
    }
}

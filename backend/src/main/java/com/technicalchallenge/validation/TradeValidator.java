package com.technicalchallenge.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.InvalidSearchException;
import com.technicalchallenge.service.ApplicationUserService;
import com.technicalchallenge.service.BookService;
import com.technicalchallenge.service.BusinessDayConventionService;
import com.technicalchallenge.service.CounterpartyService;
import com.technicalchallenge.service.CurrencyService;
import com.technicalchallenge.service.HolidayCalendarService;
import com.technicalchallenge.service.IndexService;
import com.technicalchallenge.service.LegTypeService;
import com.technicalchallenge.service.PayRecService;
import com.technicalchallenge.service.ScheduleService;
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
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PayRecService payRecService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private LegTypeService legTypeService;
    @Autowired
    private HolidayCalendarService holidayCalendarService;
    @Autowired
    private IndexService indexService;
    @Autowired
    private BusinessDayConventionService businessDayConventionService;

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

        validateBookReference(tradeDTO);
        validateCounterpartyReference(tradeDTO);
        validateUserReference(tradeDTO);

        // All reference data must exist and be valid
        validateTradeStatusReference(tradeDTO);
        validateTradeTypeReference(tradeDTO);

        logger.debug("Reference data validation passed for trade");

    }

    // Book Validation
    private void validateBookReference(TradeDTO tradeDTO) {

        Long bookId = tradeDTO.getBookId();
        String bookName = tradeDTO.getBookName();

        bookService.validateBook(bookId, bookName);

    }

    // Counterparty Validation
    private void validateCounterpartyReference(TradeDTO tradeDTO) {

        Long counterpartyId = tradeDTO.getCounterpartyId();
        String counterpartyName = tradeDTO.getCounterpartyName();

        counterpartyService.validateCounterparty(counterpartyId, counterpartyName);
    }

    // User Validation - TraderUser & Inputter User
    private void validateUserReference(TradeDTO tradeDTO) {

        Long traderUserId = tradeDTO.getTraderUserId();
        String traderUserName = tradeDTO.getTraderUserName();
        Long inputterUserId = tradeDTO.getTradeInputterUserId();
        String inputterUserName = tradeDTO.getInputterUserName();

        applicationUserService.validateUser(traderUserId, traderUserName);
        applicationUserService.validateUser(inputterUserId, inputterUserName);

    }

    // TradeStatus Validation
    private void validateTradeStatusReference(TradeDTO tradeDTO) {

        Long tradeStatusId = tradeDTO.getTradeStatusId();
        tradeStatusService.validateTradeStatus(tradeStatusId);

    }

    // TradeType Validation
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

        // TradeLeg Business rule - Floating legs must have an index specified and Fixed
        // legs must have a valid rate
        for (TradeLegDTO tradeleg : legs) {

            // Cross Leg Reference Data must exist and be valid
            validateCrossLegReferenceData(tradeleg);

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

    public void validateSearch(SearchTradeByCriteria searchTradeByCriteria) {
        // Validate date range for tradeDate
        if (searchTradeByCriteria.tradeStartDate() != null && searchTradeByCriteria.tradeEndDate() != null
                && searchTradeByCriteria.tradeEndDate().isBefore(searchTradeByCriteria.tradeStartDate())) {
            throw new InvalidSearchException("End date cannot be before start date");
        }
    }

    public void validateRSQLSearch(String query) {
        // Validate query - if the query is null or missing the exception is thrown
        if (query == null || query.isEmpty()) {
            throw new InvalidSearchException("Query must not be null or empty");
        }

    }

    // Entity Status Validation - Cross Leg
    private void validateCrossLegReferenceData(TradeLegDTO tradeLegDTO) {

        validateCurrencyReference(tradeLegDTO);
        validatePayRecReference(tradeLegDTO);
        validateScheduleReference(tradeLegDTO);
        validateLegTypeReference(tradeLegDTO);
        validateIndexReference(tradeLegDTO);
        validateHolidayCalendarReference(tradeLegDTO);
        validateBusinessDayConventionReference(tradeLegDTO);

    }

    // Currency Validation
    private void validateCurrencyReference(TradeLegDTO tradeLegDTO) {

        Long currencyId = tradeLegDTO.getCurrencyId();
        String currency = tradeLegDTO.getCurrency();

        currencyService.validateCurrency(currencyId, currency);

    }

    // Pay Rec Validation
    private void validatePayRecReference(TradeLegDTO tradeLegDTO) {

        Long payRecId = tradeLegDTO.getPayRecId();
        String payRec = tradeLegDTO.getPayReceiveFlag();

        payRecService.validatePayRec(payRecId, payRec);

    }

    // Schedule Validation
    private void validateScheduleReference(TradeLegDTO tradeLegDTO) {

        Long scheduleId = tradeLegDTO.getScheduleId();
        String schedule = tradeLegDTO.getCalculationPeriodSchedule();

        scheduleService.validateSchedule(scheduleId, schedule);

    }

    // LegType Validation
    private void validateLegTypeReference(TradeLegDTO tradeLegDTO) {

        Long legTypeId = tradeLegDTO.getLegTypeId();
        String type = tradeLegDTO.getLegType();

        legTypeService.validateLegType(legTypeId, type);

    }

    // Index Validation
    private void validateIndexReference(TradeLegDTO tradeLegDTO) {

        Long indexId = tradeLegDTO.getIndexId();
        String index = tradeLegDTO.getIndexName();

        indexService.validateIndex(indexId, index);

    }

    // HolidayCalendar Validation
    private void validateHolidayCalendarReference(TradeLegDTO tradeLegDTO) {

        Long holidayCalendarId = tradeLegDTO.getHolidayCalendarId();
        String holidayCalendar = tradeLegDTO.getHolidayCalendar();

        holidayCalendarService.validateHolidayCalendar(holidayCalendarId, holidayCalendar);

    }

    // BDC Validation - Fixing & Payment
    private void validateBusinessDayConventionReference(TradeLegDTO tradeLegDTO) {

        Long fixingBDCId = tradeLegDTO.getFixingBdcId();
        String fixingBdc = tradeLegDTO.getFixingBusinessDayConvention();
        Long paymentBDCId = tradeLegDTO.getPaymentBdcId();
        String paymentBDC = tradeLegDTO.getPaymentBusinessDayConvention();

        businessDayConventionService.validateBusinessDayConvention(fixingBDCId, fixingBdc);
        businessDayConventionService.validateBusinessDayConvention(paymentBDCId, paymentBDC);

    }

}

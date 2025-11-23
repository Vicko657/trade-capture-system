package com.technicalchallenge.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
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

import jakarta.validation.ValidationException;

/**
 * 
 * Trade Reference Data Validator
 * 
 * Contains Enity Status validation methods called in the Trade Service,
 * to validate reference data before population.
 * 
 * 
 */
@Component
public class ReferenceDataValidator {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceDataValidator.class);

    @Autowired
    private BookService bookService;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private TradeStatusService tradeStatusService;
    @Autowired
    private TradeTypeService tradeTypeService;
    @Autowired
    private ApplicationUserService applicationUserService;
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

    // 1. User, book and counterparty must be active, exist and be valid

    // Book Validation
    public Book validateBookReference(TradeDTO tradeDTO) {

        Long bookId = tradeDTO.getBookId();
        String bookName = tradeDTO.getBookName();

        if (bookId != null) {
            return bookService.findBookId(bookId);
        } else if (bookName != null) {
            return bookService.findBookName(bookName);
        }

        logger.info("Book validation passed for trade");

        throw new ValidationException("BookId or name is required");

    }

    // Counterparty Validation
    public Counterparty validateCounterpartyReference(TradeDTO tradeDTO) {

        Long counterpartyId = tradeDTO.getCounterpartyId();
        String counterpartyName = tradeDTO.getCounterpartyName();

        if (counterpartyId != null) {
            return counterpartyService.findCounterpartyId(counterpartyId);
        } else if (counterpartyName != null) {
            return counterpartyService.findCounterpartyName(counterpartyName);
        }

        logger.info("Counterparty validation passed for trade");

        throw new ValidationException("CounterpartyId or name is required");

    }

    // TraderUser Validation
    public ApplicationUser validateTraderUserReference(TradeDTO tradeDTO) {

        Long traderUserId = tradeDTO.getTraderUserId();
        String traderUserName = tradeDTO.getTraderUserName();
        String loginId = tradeDTO.getTraderUserName().toLowerCase();

        // Handle trader user by name or ID with enhanced logging
        if (traderUserName != null) {

            logger.debug("Looking up trader user by name: {}", traderUserName);
            String[] nameParts = tradeDTO.getTraderUserName().trim().split("\\s+");

            if (nameParts.length >= 1) {

                String firstName = nameParts[0];

                if (firstName != null) {

                    logger.debug("Searching for user with firstName: {}", firstName);
                    return applicationUserService.findFirstName(firstName);

                    // Try with loginId as fallback
                } else if (loginId != null) {

                    logger.warn("Trader user not found with firstName: {}", firstName);
                    return applicationUserService.findLoginId(loginId);
                }
            }

        } else if (traderUserId != null) {
            logger.warn("Trader user not found by loginId either: {}", traderUserName);
            return applicationUserService.findUserId(traderUserId);
        }

        logger.info("Trader user validation passed for trade");

        throw new ValidationException("TraderUserId, name or loginId is required");

    }

    // InputterUser Validation
    public ApplicationUser validateInputterUserReference(TradeDTO tradeDTO) {

        Long inputterUserId = tradeDTO.getTradeInputterUserId();
        String inputterUserName = tradeDTO.getInputterUserName();
        String loginId = tradeDTO.getInputterUserName().toLowerCase();

        // Handle inputter user by name or ID with enhanced logging
        if (inputterUserName != null) {

            logger.debug("Looking up inputter user by name: {}", inputterUserName);
            String[] nameParts = tradeDTO.getInputterUserName().trim().split("\\s+");

            if (nameParts.length >= 1) {

                String firstName = nameParts[0];

                if (firstName != null) {
                    logger.debug("Searching for inputter with firstName: {}", firstName);
                    return applicationUserService.findFirstName(firstName);

                    // Try with loginId as fallback
                } else if (loginId != null) {
                    logger.warn("Inputter user not found with firstName: {}", firstName);
                    return applicationUserService.findLoginId(loginId);
                }
            }

        } else if (inputterUserId != null) {
            logger.warn("Inputter user not found by loginId either: {}", inputterUserName);
            return applicationUserService.findUserId(inputterUserId);
        }

        logger.info("Inputter user validation passed for trade");

        throw new ValidationException("InputterUserId, name or loginId is required");
    }

    // 2. All reference data must exist and be valid

    // Trade Reference Data

    // TradeStatus Validation
    public TradeStatus validateTradeStatusReference(TradeDTO tradeDTO) {

        Long tradeStatusId = tradeDTO.getTradeStatusId();
        String tradeStatus = tradeDTO.getTradeStatus();

        if (tradeStatusId != null) {
            return tradeStatusService.findTradeStatus(tradeStatus);
        } else if (tradeStatus != null) {
            return tradeStatusService.findTradeStatusId(tradeStatusId);
        }

        logger.info("TradeStatus validation passed for trade");

        throw new ValidationException("TradeStatus Id or name is required");

    }

    // TradeType Validation
    public TradeType validateTradeTypeReference(TradeDTO tradeDTO) {

        Long tradeTypeId = tradeDTO.getTradeTypeId();
        String tradeType = tradeDTO.getTradeType();

        if (tradeType != null) {
            logger.debug("Looking up trade type: {}", tradeDTO.getTradeType());
            return tradeTypeService.findTradeType(tradeType);
        } else if (tradeTypeId != null) {
            return tradeTypeService.findTradeTypeId(tradeTypeId);
        }

        logger.info("TradeType validation passed for trade");

        throw new ValidationException("TradeType Id or name is required");

    }

    // TradeSubType Validation
    public TradeSubType validateTradeSubTypeReference(TradeDTO tradeDTO) {

        Long tradeSubTypeId = tradeDTO.getTradeSubTypeId();
        String tradeSubType = tradeDTO.getTradeSubType();

        if (tradeSubType != null) {
            return tradeTypeService.findTradeSubType(tradeSubType);
        } else if (tradeSubTypeId != null) {
            return tradeTypeService.findTradeSubTypeId(tradeSubTypeId);
        }

        logger.info("TradeSubType validation passed for trade");

        throw new ValidationException("TradeSubType Id or name is required");
    }

    // TradeLeg Reference Data

    // Currency Validation
    public Currency validateCurrencyReference(TradeLegDTO tradeLegDTO) {

        Long currencyId = tradeLegDTO.getCurrencyId();
        String currency = tradeLegDTO.getCurrency();

        if (currency != null) {
            return currencyService.findCurrency(currency);
        } else if (currencyId != null) {
            return currencyService.findId(currencyId);
        }

        logger.info("Currency validation passed for tradeLegs");

        throw new ValidationException("Currency Id or name is required");

    }

    // Pay Rec Validation
    public PayRec validatePayRecReference(TradeLegDTO tradeLegDTO) {

        Long payRecId = tradeLegDTO.getPayRecId();
        String payRec = tradeLegDTO.getPayReceiveFlag();

        if (payRec != null) {
            return payRecService.findPayRec(payRec);
        } else if (payRecId != null) {
            return payRecService.findId(payRecId);
        }

        throw new ValidationException("PayRec Id or name is required");

    }

    // Schedule Validation
    public Schedule validateScheduleReference(TradeLegDTO tradeLegDTO) {

        Long scheduleId = tradeLegDTO.getScheduleId();
        String schedule = tradeLegDTO.getCalculationPeriodSchedule();

        if (schedule != null) {
            return scheduleService.findSchedule(schedule);
        } else if (scheduleId != null) {
            return scheduleService.findId(scheduleId);
        }

        throw new ValidationException("Schedule Id or name is required");

    }

    // LegType Validation
    public LegType validateLegTypeReference(TradeLegDTO tradeLegDTO) {

        Long legTypeId = tradeLegDTO.getLegTypeId();
        String type = tradeLegDTO.getLegType();

        if (type != null) {
            return legTypeService.findLegType(type);
        } else if (legTypeId != null) {
            return legTypeService.findId(legTypeId);
        }

        throw new ValidationException("LegType Id or name is required");

    }

    // Index Validation
    public Index validateIndexReference(TradeLegDTO tradeLegDTO) {

        Long indexId = tradeLegDTO.getIndexId();
        String index = tradeLegDTO.getIndexName();

        if (index != null) {
            return indexService.findIndex(index);
        } else if (indexId != null) {
            return indexService.findId(indexId);
        }

        throw new ValidationException("Index Id or name is required");

    }

    // HolidayCalendar Validation
    public HolidayCalendar validateHolidayCalendarReference(TradeLegDTO tradeLegDTO) {

        Long holidayCalendarId = tradeLegDTO.getHolidayCalendarId();
        String holidayCalendar = tradeLegDTO.getHolidayCalendar();

        if (holidayCalendar != null) {
            return holidayCalendarService.findHolidayCalendar(holidayCalendar);
        } else if (holidayCalendarId != null) {
            return holidayCalendarService.findId(holidayCalendarId);
        }

        throw new ValidationException("HolidayCalendar Id or name is required");

    }

    // PaymentBusinessDayConvention Validation
    public BusinessDayConvention validatePaymentBusinessDayConventionReference(TradeLegDTO tradeLegDTO) {

        Long paymentBDCId = tradeLegDTO.getPaymentBdcId();
        String paymentBDC = tradeLegDTO.getPaymentBusinessDayConvention();

        if (paymentBDC != null) {
            return businessDayConventionService.findBDC(paymentBDC);
        } else if (paymentBDCId != null) {
            return businessDayConventionService.findId(paymentBDCId);
        }

        throw new ValidationException("PaymentBusinessDayConvention Id or name is required");

    }

    // FixingBusinessDayConvention Validation
    public BusinessDayConvention validateFixingBusinessDayConventionReference(TradeLegDTO tradeLegDTO) {

        Long fixingBDCId = tradeLegDTO.getFixingBdcId();
        String fixingBDC = tradeLegDTO.getFixingBusinessDayConvention();

        if (fixingBDC != null) {
            return businessDayConventionService.findBDC(fixingBDC);
        } else if (fixingBDCId != null) {
            return businessDayConventionService.findId(fixingBDCId);
        }

        throw new ValidationException("FixingBusinessDayConvention Id or name is required");

    }

}

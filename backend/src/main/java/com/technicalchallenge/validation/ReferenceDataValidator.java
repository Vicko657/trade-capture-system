package com.technicalchallenge.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.service.ApplicationUserService;
import com.technicalchallenge.service.BookService;
import com.technicalchallenge.service.CounterpartyService;
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

    // TradeStatus Validation
    public TradeStatus validateTradeStatusReference(TradeDTO tradeDTO) {

        Long tradeStatusId = tradeDTO.getTradeStatusId();
        String tradeStatus = tradeDTO.getTradeStatus();

        if (tradeStatusId != null) {
            return tradeStatusService.findTradeStatus(tradeStatus);
        } else if (tradeStatus != null) {
            return tradeStatusService.findById(tradeStatusId);
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

}

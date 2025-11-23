package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.exceptions.EntityNotFoundException;
import com.technicalchallenge.exceptions.InActiveException;
import com.technicalchallenge.exceptions.referencedata.TradeNotFoundException;
import com.technicalchallenge.exceptions.referencedata.TradeStatusNotFoundException;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.*;
import com.technicalchallenge.repository.*;
import com.technicalchallenge.validation.ReferenceDataValidator;
import com.technicalchallenge.validation.TradeValidator;
import com.technicalchallenge.validation.ValidationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Trade service class provides business logic and operations relating
 * to trading CRUD operations and search functionalities.
 * 
 * - Get, Create, Amend, Delete, Terminate and Cancel Trades
 * - RSQL, Filtered search and a muilti criteria
 * 
 *
 * FUTURE: Work on refactoring the trade service, by making sure all the
 * services handle their own business logic.
 * 
 * The trade service is tightly coupled, which makes the code very
 * long and repetitive by using direct repositories. I decided to add the
 * validations for the reference data in their own individual services called in
 * the {@link TradeValidator}.
 * 
 * Due to time constraints, I continued to use the Optional objects, so the code
 * will not be broken where its implemented in other classes.
 *
 */
@Service
@Transactional
public class TradeService {
    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private TradeLegService tradeLegService;
    @Autowired
    private CashflowService cashflowService;
    @Autowired
    private TradeStatusRepository tradeStatusRepository;
    @Autowired
    private TradeValidator tradeValidator;
    @Autowired
    private ReferenceDataValidator referenceDataValidator;
    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private TradeMapper tradeMapper;

    /**
     * Trade: Return all trades that have been created on the system
     * 
     * Privilege: READ TRADE
     * 
     * @return all trades
     */
    public List<Trade> getAllTrades() {

        logger.info("Retrieving all trades");

        Long userId = authorizationService.getCurrentUserId();

        authorizationService.validateUserPrivileges(userId, "READ_TRADE", null);

        return tradeRepository.findAll();
    }

    /**
     * Trade: Returns a trade with a tradeId
     * 
     * <p>
     * Used to access a specific trade in the trading application.
     * 
     * Exception thrown if trade is not found.
     * {@link EntityNotFoundException}
     * </p>
     * 
     * Privilege: READ TRADE
     * 
     * @param tradeId trade unique identifier
     * @return a single trade
     */
    public Trade getTradeById(Long tradeId) {

        logger.debug("Retrieving trade by id: {}", tradeId);

        Long userId = authorizationService.getCurrentUserId();

        authorizationService.validateUserPrivileges(userId, "READ_TRADE", null);

        return tradeRepository.findByTradeIdAndActiveTrue(tradeId)
                .orElseThrow(() -> new TradeNotFoundException("tradeId", tradeId));

    }

    /**
     * Trade: Create's Trades
     * 
     * <p>
     * Creates a trade entity using mapping with the dto.
     * 
     * Also creates the tradelegs and generates cashflows.
     * cashflows.
     * 
     * Privilege: CREATE TRADE
     * 
     * Validation was implemented for: User privileges,
     * Trade Buisness rules and Cross leg Rules
     * 
     * Using {@link TradeValidator} {@link AuthorizationService}
     * </p>
     * 
     * 
     * 
     * FUTURE: Would utilise the trademapper fully in the service
     * to map to the entity and back to the dto, to remove repetitive code.
     * Would fix the cashflows generation bug, when creating
     * a trade multiple (100s) cashflows were generated.
     * 
     * @param tradeDTO trade's data object
     * @return a trade entity with tradelegs and cashflows
     */
    @Transactional
    public Trade createTrade(TradeDTO tradeDTO) {
        logger.info("Creating new trade with ID: {}", tradeDTO.getTradeId());

        // Validates User - Create Trade
        Long userId = authorizationService.getCurrentUserId();
        authorizationService.validateUserPrivileges(userId, "CREATE_TRADE", tradeDTO);

        // Validate trade business rules
        ValidationResult tradeBusinessValidation = tradeValidator.validateTradeBusinessRules(tradeDTO);
        tradeBusinessValidation.throwifNotValid();

        // Generate trade ID if not provided
        if (tradeDTO.getTradeId() == null) {
            // Generate sequential trade ID starting from 10000
            Long generatedTradeId = generateNextTradeId();
            tradeDTO.setTradeId(generatedTradeId);
            logger.info("Generated trade ID: {}", generatedTradeId);
        }

        // Create trade entity
        Trade trade = tradeMapper.toEntity(tradeDTO);
        trade.setVersion(1);
        trade.setActive(true);
        trade.setCreatedDate(LocalDateTime.now());
        trade.setLastTouchTimestamp(LocalDateTime.now());

        // Set default trade status to NEW if not provided
        if (tradeDTO.getTradeStatus() == null) {
            tradeDTO.setTradeStatus("NEW");
        }

        // Populate reference data
        populateReferenceDataByName(trade, tradeDTO);

        Trade savedTrade = tradeRepository.save(trade);

        // Create trade legs and cashflows
        createTradeLegsWithCashflows(tradeDTO, savedTrade);

        logger.info("Successfully created trade with ID: {}", savedTrade.getTradeId());
        return savedTrade;
    }

    // NEW METHOD: For controller compatibility
    @Transactional
    public Trade saveTrade(Trade trade, TradeDTO tradeDTO) {
        logger.info("Saving trade with ID: {}", trade.getTradeId());

        // If this is an existing trade (has ID), handle as amendment
        if (trade.getId() != null) {
            return amendTrade(trade.getTradeId(), tradeDTO);
        } else {
            return createTrade(tradeDTO);
        }
    }

    // FIXED: Populate reference data by names from DTO.
    // UPDATED: Using the ReferenceDataValidator instead of repositories to reduce
    // code repetition.
    public void populateReferenceDataByName(Trade trade, TradeDTO tradeDTO) {
        logger.debug("Populating reference data for trade");

        // Populate Book
        Book book = referenceDataValidator.validateBookReference(tradeDTO);

        if (!book.isActive()) {
            throw new InActiveException("Book must be active to populate a Trade");
        }
        logger.debug("Book is active");

        trade.setBook(book);

        // Populate Counterparty
        Counterparty counterparty = referenceDataValidator.validateCounterpartyReference(tradeDTO);

        if (!counterparty.isActive()) {
            throw new InActiveException("Counterparty must be active to populate a Trade");
        }
        logger.debug("Counterparty is active");

        trade.setCounterparty(counterparty);

        // Populate TradeStatus
        TradeStatus tradeStatus = referenceDataValidator.validateTradeStatusReference(tradeDTO);
        trade.setTradeStatus(tradeStatus);

        // Populate other reference data
        populateUserReferences(trade, tradeDTO);
        populateTradeTypeReferences(trade, tradeDTO);
    }

    private void populateUserReferences(Trade trade, TradeDTO tradeDTO) {

        // Populate TraderUserReference
        ApplicationUser traderUser = referenceDataValidator.validateTraderUserReference(tradeDTO);

        // Checks if traderUser is active
        if (!traderUser.isActive()) {
            throw new InActiveException("TraderUser must be active to populate a Trade");
        }

        trade.setTraderUser(traderUser);

        // Populate InputterUserReference
        ApplicationUser inputterUser = referenceDataValidator.validateInputterUserReference(tradeDTO);

        // Checks if traderUser is active
        if (!inputterUser.isActive()) {
            throw new InActiveException("TraderUser must be active to populate a Trade");
        }

        trade.setTradeInputterUser(inputterUser);

    }

    private void populateTradeTypeReferences(Trade trade, TradeDTO tradeDTO) {

        // Populate TradeType
        TradeType tradeType = referenceDataValidator.validateTradeTypeReference(tradeDTO);

        trade.setTradeType(tradeType);

        // Populate TradeSubType
        TradeSubType tradeSubType = referenceDataValidator.validateTradeSubTypeReference(tradeDTO);

        trade.setTradeSubType(tradeSubType);
    }

    // NEW METHOD: Delete trade (mark as cancelled)
    @Transactional
    public void deleteTrade(Long tradeId) {
        logger.info("Deleting (cancelling) trade with ID: {}", tradeId);
        cancelTrade(tradeId);
    }

    /**
     * Trade: Amends the trade
     * 
     * <p>
     * Modifiys and updates a exisiting trade's details and
     * creates a different version.
     * 
     * Privilege: AMEND TRADE
     * </p>
     * 
     * @param tradeId  trade unique identifier
     * @param tradeDTO trade data object
     * @return A new version of the trade
     */
    @Transactional
    public Trade amendTrade(Long tradeId, TradeDTO tradeDTO) {

        logger.info("Amending trade with ID: {}", tradeId);

        // Validates User - Amend Trade
        Long userId = authorizationService.getCurrentUserId();
        authorizationService.validateUserPrivileges(userId, " AMEND_TRADE", tradeDTO);

        Trade existingTrade = getTradeById(tradeId);

        // Deactivate existing trade
        existingTrade.setActive(false);
        existingTrade.setDeactivatedDate(LocalDateTime.now());
        tradeRepository.save(existingTrade);

        // Create new version
        Trade amendedTrade = tradeMapper.toEntity(tradeDTO);
        amendedTrade.setTradeId(tradeId);
        amendedTrade.setVersion(existingTrade.getVersion() + 1);
        amendedTrade.setActive(true);
        amendedTrade.setCreatedDate(LocalDateTime.now());
        amendedTrade.setLastTouchTimestamp(LocalDateTime.now());

        // Populate reference data
        populateReferenceDataByName(amendedTrade, tradeDTO);

        // Set status to AMENDED
        TradeStatus amendedStatus = tradeStatusRepository.findByTradeStatus("AMENDED")
                .orElseThrow(() -> new TradeStatusNotFoundException("AMENDED"));
        amendedTrade.setTradeStatus(amendedStatus);

        Trade savedTrade = tradeRepository.save(amendedTrade);

        // Create new trade legs and cashflows
        createTradeLegsWithCashflows(tradeDTO, savedTrade);

        logger.info("Successfully amended trade with ID: {}", savedTrade.getTradeId());
        return savedTrade;
    }

    /**
     * Trade: Terminating a trade
     * 
     * <p>
     * Ends the trade's lifecycle
     * </p>
     * 
     * Privilege: TERMINATE TRADE
     * 
     * @param tradeId trade unique identifier
     * @return Updates last touch timestamp and updates tradeStatus
     */
    @Transactional
    public Trade terminateTrade(Long tradeId) {
        logger.info("Terminating trade with ID: {}", tradeId);

        Trade trade = getTradeById(tradeId);

        // Validates User - Terminate Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);
        Long userId = authorizationService.getCurrentUserId();
        authorizationService.validateUserPrivileges(userId, "TERMINATE_TRADE", tradeDTO);

        TradeStatus terminatedStatus = tradeStatusRepository.findByTradeStatus("TERMINATED")
                .orElseThrow(() -> new TradeStatusNotFoundException("TERMINATED"));

        trade.setTradeStatus(terminatedStatus);
        trade.setLastTouchTimestamp(LocalDateTime.now());

        return tradeRepository.save(trade);
    }

    /**
     * Trade: Cancels trade
     * 
     * <p>
     * Cancels trade's unfulfilled order
     * </p>
     * 
     * @param tradeId trade unique identifier
     * @return new status and updated timestamp
     */
    @Transactional
    public Trade cancelTrade(Long tradeId) {

        logger.info("Cancelling trade with ID: {}", tradeId);

        Trade trade = getTradeById(tradeId);

        // Validates User - Cancel Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);
        Long userId = authorizationService.getCurrentUserId();
        authorizationService.validateUserPrivileges(userId, "CANCEL_TRADE", tradeDTO);

        TradeStatus cancelledStatus = tradeStatusRepository.findByTradeStatus("CANCELLED")
                .orElseThrow(() -> new TradeStatusNotFoundException("CANCELLED"));

        trade.setTradeStatus(cancelledStatus);
        trade.setLastTouchTimestamp(LocalDateTime.now());

        return tradeRepository.save(trade);
    }

    // NEW METHOD: Creates Trade Legs with Cashflows (Cleaner Seperation of Concerns
    // and easier to debug)
    public void createTradeLegsWithCashflows(TradeDTO tradeDTO, Trade trade) {

        List<TradeLeg> tradeLegs = tradeLegService.createTradeLegs(tradeDTO, trade);
        trade.setTradeLegs(tradeLegs);

        if (tradeDTO.getTradeStartDate() != null && tradeDTO.getTradeMaturityDate() != null) {

            for (TradeLeg leg : tradeLegs) {
                List<Cashflow> cashflows = cashflowService.generateCashflows(leg, tradeDTO.getTradeStartDate(),
                        tradeDTO.getTradeMaturityDate());

                leg.setCashflows(cashflows);
            }

        }

    }

    // NEW METHOD: Generate the next trade ID (sequential)
    private Long generateNextTradeId() {
        // For simplicity, using a static variable. In real scenario, this should be
        // atomic and thread-safe.
        return 10000L + tradeRepository.count();
    }

}

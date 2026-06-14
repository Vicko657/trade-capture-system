package com.technicalchallenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.ValidationException;
import com.technicalchallenge.exceptions.referencedata.TradeNotFoundException;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.repository.TradeRepository;
import com.technicalchallenge.repository.TradeStatusRepository;
import com.technicalchallenge.validation.ReferenceDataValidator;
import com.technicalchallenge.validation.TradeValidator;
import com.technicalchallenge.validation.ValidationResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeLegService tradeLegService;

    @Mock
    private CashflowService cashflowService;

    @Mock
    private TradeStatusRepository tradeStatusRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private TradeValidator tradeValidator;

    @Mock
    private ReferenceDataValidator referenceDataValidator;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private TradeMapper tradeMapper;

    @Mock
    private TradeLegMapper tradeLegMapper;

    @InjectMocks
    private TradeService tradeService;

    private TradeDTO tradeDTO;
    private Trade trade;
    private Book book;
    private Counterparty counterparty;
    private TradeStatus tradeStatus;
    private TradeLeg tradeLeg1, tradeLeg2;
    private TradeLegDTO leg1, leg2;
    private List<Cashflow> cashflowList1, cashflowList2;
    private Cashflow cashflow1, cashflow2;
    private Schedule schedule;
    private ApplicationUser traderUser, inputterUser;
    private TradeType tradeType;
    private TradeSubType tradeSubType;
    private Currency currency;
    private LegType legType;
    private HolidayCalendar holidayCalendar;
    private PayRec payRec;
    private BusinessDayConvention paymentBusinessDayConvention;
    private BusinessDayConvention fixingBusinessDayConvention;

    private ValidationResult inValidResult, validResult;
    private List<String> errors;

    @BeforeEach
    void setUp() {
        // Set up test data
        tradeMapper = new TradeMapper(tradeLegMapper);
        objectMapper.registerModule(new JavaTimeModule());

        tradeService = new TradeService(tradeRepository, tradeLegService, cashflowService, tradeStatusRepository,
                tradeValidator, referenceDataValidator, authorizationService, tradeMapper);

        // TraderUser Reference
        traderUser = new ApplicationUser();
        traderUser.setId(1L);
        traderUser.setActive(true);
        traderUser.setFirstName("John");
        traderUser.setLastName("Smith");

        // TraderUser Reference
        inputterUser = new ApplicationUser();
        inputterUser.setId(3L);
        inputterUser.setActive(true);
        inputterUser.setFirstName("Jess");
        inputterUser.setLastName("Abraham");

        // Book Reference
        book = new Book();
        book.setActive(true);
        book.setId(5L);
        book.setBookName("FX-BOOK-1");

        // Counterparty Reference
        counterparty = new Counterparty();
        counterparty.setActive(true);
        counterparty.setId(7L);
        counterparty.setName("BigBank");

        // Trade Status Reference
        tradeStatus = new TradeStatus();
        tradeStatus.setId(9L);
        tradeStatus.setTradeStatus("NEW");

        // Trade Status Reference
        tradeType = new TradeType();
        tradeType.setId(1L);
        tradeType.setTradeType("Swap");

        // Trade Status Reference
        tradeSubType = new TradeSubType();
        tradeSubType.setId(1L);
        tradeSubType.setTradeSubType("IR Swap");

        // Schedule Reference
        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setSchedule("1M");

        currency = new Currency();
        currency.setId(1L);
        currency.setCurrency("USD");

        legType = new LegType();
        legType.setId(1L);
        legType.setType("Fixed");

        paymentBusinessDayConvention = new BusinessDayConvention();
        paymentBusinessDayConvention.setId(1L);
        paymentBusinessDayConvention.setBdc("Following");

        fixingBusinessDayConvention = new BusinessDayConvention();
        fixingBusinessDayConvention.setId(2L);
        fixingBusinessDayConvention.setBdc("Following");

        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setSchedule("Quarterly");

        holidayCalendar = new HolidayCalendar();
        holidayCalendar.setId(1L);
        holidayCalendar.setHolidayCalendar("NY");

        currency = new Currency();
        currency.setId(1L);
        currency.setCurrency("USD");

        // TradeDTO - DTO
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(100001L);
        tradeDTO.setVersion(1);
        tradeDTO.setActive(true);
        tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
        tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
        tradeDTO.setTradeStatusId(tradeStatus.getId());
        tradeDTO.setCounterpartyId(counterparty.getId());
        tradeDTO.setBookId(book.getId());

        // Trade - Entity
        trade = new Trade();
        trade.setId(1L);
        trade.setTradeId(100001L);
        trade.setTradeStartDate(tradeDTO.getTradeStartDate());
        trade.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());
        trade.setBook(book);
        trade.setCounterparty(counterparty);
        trade.setTradeStatus(tradeStatus);
        trade.setVersion(1);

        // Trade Leg Reference
        leg1 = new TradeLegDTO();
        leg1.setLegId(3L);
        leg1.setNotional(BigDecimal.valueOf(1000000));
        leg1.setRate(0.05);
        leg1.setCurrencyId(2L);
        leg1.setCurrency("USD");
        leg1.setCalculationPeriodSchedule("1M");
        leg1.setHolidayCalendarId(1L);
        leg1.setHolidayCalendar("NY");
        leg1.setPayRecId(2L);
        leg1.setPayReceiveFlag("Pay");
        leg1.setPaymentBdcId(2L);
        leg1.setPaymentBusinessDayConvention("Following");
        leg1.setFixingBdcId(4L);
        leg1.setFixingBusinessDayConvention("Following");

        leg2 = new TradeLegDTO();
        leg2.setLegId(4L);
        leg2.setNotional(BigDecimal.valueOf(1000000));
        leg2.setRate(0.05);
        leg2.setCurrencyId(2L);
        leg2.setCurrency("USD");
        leg2.setCalculationPeriodSchedule("1M");
        leg2.setHolidayCalendarId(1L);
        leg2.setHolidayCalendar("NY");
        leg2.setPayRecId(2L);
        leg2.setPayReceiveFlag("Recieve");
        leg2.setPaymentBdcId(2L);
        leg2.setPaymentBusinessDayConvention("Following");
        leg2.setFixingBdcId(4L);
        leg2.setFixingBusinessDayConvention("Following");

        tradeLeg1 = new TradeLeg();
        tradeLeg1.setLegId(1L);
        tradeLeg1.setNotional(BigDecimal.valueOf(1000000));
        tradeLeg1.setRate(0.05);
        tradeLeg1.setCalculationPeriodSchedule(schedule);

        tradeLeg2 = new TradeLeg();
        tradeLeg2.setLegId(2L);
        tradeLeg2.setNotional(BigDecimal.valueOf(1000000));
        tradeLeg2.setRate(0.05);
        tradeLeg2.setCalculationPeriodSchedule(schedule);

        // Assigned TradeLegs
        trade.setTradeLegs(List.of(tradeLeg1, tradeLeg2));
        tradeDTO.setTradeLegs(List.of(leg1, leg2));

        // Cashflow Reference
        cashflowList1 = new ArrayList<Cashflow>();
        cashflowList2 = new ArrayList<Cashflow>();
        cashflow1 = new Cashflow();
        cashflow2 = new Cashflow();
        cashflow1.setTradeLeg(tradeLeg1);
        cashflow2.setTradeLeg(tradeLeg2);

        // Assigned Cashflows
        tradeLeg1.setCashflows(cashflowList1);
        tradeLeg2.setCashflows(cashflowList2);

        // Trade Business Validation
        errors = new ArrayList<>();
        inValidResult = ValidationResult.isNotValid(errors);
        validResult = ValidationResult.isValid();

    }

    private void populateReferenceData(Trade trade, TradeDTO tradeDTO) {
        when(referenceDataValidator.validateBookReference(tradeDTO)).thenReturn(book);
        when(referenceDataValidator.validateCounterpartyReference(tradeDTO)).thenReturn(counterparty);
        when(referenceDataValidator.validateTraderUserReference(tradeDTO)).thenReturn(traderUser);
        when(referenceDataValidator.validateInputterUserReference(tradeDTO)).thenReturn(inputterUser);
        when(referenceDataValidator.validateTradeStatusReference(tradeDTO)).thenReturn(tradeStatus);
        when(referenceDataValidator.validateTradeTypeReference(tradeDTO)).thenReturn(tradeType);
        when(referenceDataValidator.validateTradeSubTypeReference(tradeDTO)).thenReturn(tradeSubType);
        tradeService.populateReferenceDataByName(trade, tradeDTO);
    }

    private void tradeStatus(String status, Trade trade) {
        TradeStatus tradeStatus = new TradeStatus();
        tradeStatus.setTradeStatus(status);
        when(tradeStatusRepository.findByTradeStatus(status))
                .thenReturn(Optional.of(tradeStatus));
        trade.setTradeStatus(tradeStatus);
    }

    private void findByTradeId(Long tradeId, Trade trade) {
        when(tradeRepository.findByTradeIdAndActiveTrue(tradeId)).thenReturn(Optional.of(trade));
    }

    @Test
    void testGetAllTrades_Success() {

        // Given
        List<Trade> trades = new ArrayList<>();
        for (Long x = 0L; x < 2060L; x++) {
            Trade trade = new Trade();
            trade.setId(x);
            trades.add(trade);
        }

        when(tradeRepository.findAll()).thenReturn(trades);

        // When
        List<Trade> result = tradeService.getAllTrades();

        // Then
        assertNotNull(result);
        assertEquals(2060L, result.size());
        verify(tradeRepository).findAll();
    }

    /**
     * Tests if creating a trade is successful
     */
    @Test
    void testCreateTrade_Success() {

        // Given
        TradeDTO tradeDTO1 = new TradeDTO();
        tradeDTO1.setId(1000L);
        tradeDTO1.setTradeDate(LocalDate.of(2026, 9, 15));
        tradeDTO1.setTradeStartDate(LocalDate.of(2026, 9, 17));
        tradeDTO1.setTradeMaturityDate(LocalDate.of(2026, 9, 17));
        tradeDTO1.setTradeExecutionDate(LocalDate.of(2026, 9, 17));
        tradeDTO1.setCounterpartyId(counterparty.getId());
        tradeDTO1.setBookId(book.getId());
        tradeDTO1.setTradeType("Swap");
        tradeDTO1.setTradeSubType("IR Swap");

        when(tradeValidator.validateTradeBusinessRules(tradeDTO1)).thenReturn(validResult);

        tradeDTO1.setTradeId(10003L);

        Trade newTrade = tradeMapper.toEntity(tradeDTO1);
        newTrade.setVersion(1);
        newTrade.setActive(true);
        newTrade.setCreatedDate(LocalDateTime.now());
        newTrade.setLastTouchTimestamp(LocalDateTime.now());

        populateReferenceData(newTrade, tradeDTO1);

        when(tradeRepository.save(any(Trade.class))).thenReturn(newTrade);

        when(tradeLegService.createTradeLegs(tradeDTO1, newTrade)).thenReturn(List.of(tradeLeg1, tradeLeg2));
        when(cashflowService.generateCashflows(tradeLeg1, tradeDTO1.getTradeStartDate(),
                tradeDTO1.getTradeMaturityDate())).thenReturn(List.of(cashflow1, cashflow2));
        newTrade.setTradeLegs(List.of(tradeLeg1, tradeLeg2));
        tradeDTO1.setTradeLegs(List.of(leg1, leg2));

        // When
        Trade result = tradeService.createTrade(tradeDTO1);

        // Then
        assertNotNull(result);
        assertEquals(10003L, result.getTradeId());
        assertEquals("FX-BOOK-1", result.getBook().getBookName());
        assertEquals(LocalDate.of(2026, 9, 17), result.getTradeStartDate());
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void testGetTradeById_Found() {

        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));

        // When
        Trade result = tradeService.getTradeById(100001L);

        // Then
        assertNotNull(result);
        assertEquals(100001L, result.getTradeId());
    }

    @Test
    void testGetTradeById_NotFound() {

        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When
        TradeNotFoundException exception = assertThrows(TradeNotFoundException.class, () -> {
            tradeService.getTradeById(999L);
        });
        // Then
        assertTrue(exception.getMessage().contains("Trade is not found with tradeId: 999"));

    }

    /**
     * Tests if amending a trade is successful
     */
    @Test
    void testAmendTrade_Success() {

        // Given
        trade.setTradeMaturityDate(LocalDate.of(2026, 5, 30));
        tradeDTO.setTradeMaturityDate(trade.getTradeMaturityDate());
        findByTradeId(100001L, trade);
        trade.setActive(false);
        trade.setDeactivatedDate(LocalDateTime.now());

        Trade amendedTrade = tradeMapper.toEntity(tradeDTO);
        amendedTrade.setTradeId(100001L);
        amendedTrade.setVersion(trade.getVersion() + 1);
        amendedTrade.setActive(true);
        amendedTrade.setCreatedDate(LocalDateTime.now());
        amendedTrade.setLastTouchTimestamp(LocalDateTime.now());

        populateReferenceData(amendedTrade, tradeDTO);
        tradeStatus("AMENDED", amendedTrade);

        when(tradeRepository.save(any(Trade.class))).thenReturn(amendedTrade);
        when(tradeLegService.createTradeLegs(tradeDTO, amendedTrade)).thenReturn(List.of(tradeLeg1, tradeLeg2));

        // When - Checks if the trade has been amended
        Trade result = tradeService.amendTrade(100001L, tradeDTO);

        // Then - Verifies the trade has been amended
        assertNotNull(result);
        assertEquals(false, trade.getActive());
        assertEquals(100001L, result.getTradeId());
        assertEquals(LocalDate.of(2026, 5, 30), result.getTradeMaturityDate());
        assertEquals(2, result.getVersion());
        assertEquals(true, result.getActive());
        assertEquals("AMENDED", result.getTradeStatus().getTradeStatus());
        verify(tradeRepository, times(2)).save(any(Trade.class));
    }

    @Test
    void testAmendTrade_TradeNotFound() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.amendTrade(999L, tradeDTO);
        });

        assertTrue(exception.getMessage().contains("Trade is not found with tradeId: 999"));
    }

    /**
     * Tests if business rules validation validates
     */
    @Test
    void testCreateTradeValidationResult_Success() {

        // Given - Validation Result & mocked both validation methods, saving a
        // trade and tradelegs
        when(tradeValidator.validateTradeBusinessRules(tradeDTO)).thenReturn(validResult);
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(tradeLegService.createTradeLegs(tradeDTO, trade)).thenReturn(List.of(tradeLeg1, tradeLeg2));
        populateReferenceData(trade, tradeDTO);

        // When - createTrade method call
        Trade result = tradeService.createTrade(tradeDTO);

        // Then - Verifies the trade was created and there was no errors
        assertNotNull(result);
        verify(tradeValidator).validateTradeBusinessRules(tradeDTO);
    }

    /**
     * Tests if business rules validation throws a exception
     */
    @Test
    void testCreateTradeValidationBusinessRules_ShouldFail() {

        // Given - List of errors, validation result, before trade date & mocked
        // stubbing
        trade.setTradeStartDate(LocalDate.of(2025, 1, 10));
        errors.add("Start date cannot be before trade date");
        when(tradeValidator.validateTradeBusinessRules(tradeDTO)).thenReturn(inValidResult);

        // When - A ValidationException is thrown and assertThrows returns the exception
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        // Then - Verifies the exception was thrown
        assertEquals("Start date cannot be before trade date", exception.getErrors().get(0));
        assertEquals(1, exception.getErrors().size());
        verify(tradeRepository, never()).save(any());
    }

    @Test
    void testTerminateTrade_Success() {

        // Given
        findByTradeId(1L, trade);
        tradeMapper.toDto(trade);
        tradeStatus("TERMINATED", trade);
        trade.setLastTouchTimestamp(LocalDateTime.now());
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // When
        Trade result = tradeService.terminateTrade(trade.getId());

        // Then
        assertNotNull(result);
        assertEquals("TERMINATED", result.getTradeStatus().getTradeStatus());
        verify(tradeRepository).save(any(Trade.class));
    }

    @Test
    void testCancelTrade_Success() {

        // Given
        findByTradeId(1L, trade);
        tradeMapper.toDto(trade);
        tradeStatus("CANCELLED", trade);
        trade.setLastTouchTimestamp(LocalDateTime.now());
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // When
        Trade result = tradeService.cancelTrade(trade.getId());

        // Then
        assertNotNull(result);
        assertEquals("CANCELLED", result.getTradeStatus().getTradeStatus());
        verify(tradeRepository).save(any(Trade.class));
    }

}

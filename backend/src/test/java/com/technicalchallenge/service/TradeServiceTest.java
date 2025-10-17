package com.technicalchallenge.service;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.CashflowRepository;
import com.technicalchallenge.repository.CounterpartyRepository;
import com.technicalchallenge.repository.TradeLegRepository;
import com.technicalchallenge.repository.TradeRepository;
import com.technicalchallenge.repository.TradeStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeLegRepository tradeLegRepository;

    @Mock
    private CashflowRepository cashflowRepository;

    @Mock
    private TradeStatusRepository tradeStatusRepository;

    @Mock
    private AdditionalInfoService additionalInfoService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @InjectMocks
    private TradeService tradeService;

    private TradeDTO tradeDTO;
    private Trade trade;
    private Book book;
    private Counterparty counterparty;
    private TradeStatus tradeStatus;
    private TradeLegDTO leg1;
    private TradeLegDTO leg2;
    private TradeLeg tradeLeg;

    @BeforeEach
    void setUp() {
        // Set up test data

        // TradeDTO - DTO
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(100001L);
        tradeDTO.setVersion(1);
        tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
        tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));

        // Trade Leg Reference
        tradeLeg = new TradeLeg();

        leg1 = new TradeLegDTO();
        leg1.setNotional(BigDecimal.valueOf(1000000));
        leg1.setRate(0.05);

        leg2 = new TradeLegDTO();
        leg2.setNotional(BigDecimal.valueOf(1000000));
        leg2.setRate(0.0);

        tradeDTO.setTradeLegs(Arrays.asList(leg1, leg2));

        // Book Reference
        book = new Book();
        book.setId(5L);
        book.setBookName("TestBookC");

        tradeDTO.setBookId(book.getId());
        tradeDTO.setBookName(book.getBookName());

        // Counterparty Reference
        counterparty = new Counterparty();
        counterparty.setId(7L);
        counterparty.setName("TestCounterpartyC");

        tradeDTO.setCounterpartyId(counterparty.getId());
        tradeDTO.setCounterpartyName(counterparty.getName());

        // Trade Status Reference
        tradeStatus = new TradeStatus();
        tradeStatus.setId(9L);
        tradeStatus.setTradeStatus("NEW");

        tradeDTO.setTradeStatusId(tradeStatus.getId());
        tradeDTO.setTradeStatus(tradeStatus.getTradeStatus());

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
    }

    /**
     * Tests if creating a trade is successful
     */
    @Test
    void testCreateTrade_Success() {

        // Given - Set the new tradeDTO, new trade and data in the setUp() method
        // tradeStatus.setTradeStatus("NEW");
        // Problem: RuntimeException error was thrown, "Book not found or not set"
        // Fixed: Added stubbing statements to populate reference data and validate the
        // data
        when(bookRepository.findByBookName("TestBookC")).thenReturn(Optional.of(book));
        when(counterpartyRepository.findByName("TestCounterpartyC")).thenReturn(Optional.of(counterparty));
        when(tradeStatusRepository.findByTradeStatus("NEW"))
                .thenReturn(Optional.of(tradeStatus));

        // Mocked saving a new trade
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // Problem: NullPointerException was thrown, get.tradeLegid() was null
        // Fixed: Mocked saving a new trade leg entity
        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(tradeLeg);

        // When - Checks that the trade has been created
        tradeService.populateReferenceDataByName(trade, tradeDTO);
        Trade result = tradeService.createTrade(tradeDTO);

        // Then - Verifies that the results are not null and the trade has been saved
        assertNotNull(result);
        assertEquals(100001L, result.getTradeId());
        assertEquals("TestBookC", result.getBook().getBookName());
        assertEquals(LocalDate.of(2025, 1, 17), result.getTradeStartDate());
        verify(tradeRepository).save(any(Trade.class));
    }

    /**
     * Tests if Invalid Dates fail when a trade is created
     */
    @Test
    void testCreateTrade_InvalidDates_ShouldFail() {
        // Given - The Trade Start Date 10/1/2025 is before the Trade Date 15/1/2025
        // which is invalid
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 10)); // Before trade date

        // When & Then
        // A runtime exception is thrown and assertThrows returns the
        // exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        // Problem: This assertion is intentionally wrong - candidates need to fix it
        // Fixed: Changed "Wrong error message" to "Start date cannot be before trade
        // date" which matches the error message in validateTradeCreation() method
        assertEquals("Start date cannot be before trade date", exception.getMessage());
    }

    @Test
    void testCreateTrade_InvalidLegCount_ShouldFail() {
        // Given
        tradeDTO.setTradeLegs(Arrays.asList(new TradeLegDTO())); // Only 1 leg

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        assertTrue(exception.getMessage().contains("exactly 2 legs"));
    }

    @Test
    void testGetTradeById_Found() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));

        // When
        Optional<Trade> result = tradeService.getTradeById(100001L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(100001L, result.get().getTradeId());
    }

    @Test
    void testGetTradeById_NotFound() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When
        Optional<Trade> result = tradeService.getTradeById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    /**
     * Tests if amending a trade is successful
     */
    @Test
    void testAmendTrade_Success() {
        // Given - Finds existing trade id
        // Finds and disables existing trade with 100001L
        when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));
        trade.setActive(false);
        trade.setDeactivatedDate(LocalDateTime.now());

        // New trade is created with the same Id as the existing trade
        Trade amendedTrade = new Trade();
        amendedTrade.setTradeId(100001L);
        // Problem: NullPointerException was thrown, trade.getVersion() was null
        // Fixed: Set existing trade version to 1 and set the amended trade version to
        amendedTrade.setVersion(trade.getVersion() + 1);

        // Creates a new trade status and sets to amended trade
        TradeStatus amendedStatus = new TradeStatus();
        amendedStatus.setTradeStatus("AMENDED");
        amendedTrade.setTradeStatus(amendedStatus);
        tradeDTO.setTradeStatus(amendedStatus.getTradeStatus());
        when(tradeStatusRepository.findByTradeStatus("AMENDED"))
                .thenReturn(Optional.of(amendedStatus));

        // Changed the trades maturity date to check if the trade has been amended
        amendedTrade.setTradeMaturityDate(LocalDate.of(2026, 5, 30));
        tradeDTO.setTradeMaturityDate(amendedTrade.getTradeMaturityDate());

        // Mocked saving a new and old trade
        when(tradeRepository.save(any(Trade.class))).thenReturn(amendedTrade);

        // Problem: NullPointerException was thrown, get.tradeLegid() was null
        // Fixed: Mocked saving a new trade leg entity
        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(tradeLeg);

        // When - Checks if the trade has been amended
        Trade result = tradeService.amendTrade(100001L, tradeDTO);

        // Then - Verifies the trade has been amended
        assertNotNull(result); // Amended trade is not null
        assertEquals(false, trade.getActive()); // Trade is not active
        assertEquals(100001L, result.getTradeId()); // Amended trade has the same Id as the existing trade Id
        assertEquals(LocalDate.of(2026, 5, 30), result.getTradeMaturityDate()); // The maturity date has changed
        assertEquals(2, result.getVersion()); // Amended trade version is now 2
        assertEquals(true, result.getActive()); // Amended trade is active
        verify(tradeRepository, times(2)).save(any(Trade.class)); // Save old and new
    }

    @Test
    void testAmendTrade_TradeNotFound() {
        // Given
        when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tradeService.amendTrade(999L, tradeDTO);
        });

        assertTrue(exception.getMessage().contains("Trade not found"));
    }

    // This test has a deliberate bug for candidates to find and fix
    @Test
    void testCashflowGeneration_MonthlySchedule() {
        // This test method is incomplete and has logical errors
        // Candidates need to implement proper cashflow testing

        // Given - setup is incomplete
        TradeLeg leg = new TradeLeg();
        leg.setNotional(BigDecimal.valueOf(1000000));

        // When - method call is missing

        // Then - assertions are wrong/missing
        assertEquals(1, 12); // This will always fail - candidates need to fix
    }
}

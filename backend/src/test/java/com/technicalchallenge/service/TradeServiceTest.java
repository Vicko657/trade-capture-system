package com.technicalchallenge.service;

import com.technicalchallenge.dto.PaginationDTO;
import com.technicalchallenge.dto.SearchTradeByCriteria;
import com.technicalchallenge.dto.SortDTO;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.InvalidRsqlQueryException;
import com.technicalchallenge.exceptions.InvalidSearchCriteriaException;
import com.technicalchallenge.exceptions.ValidationException;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.repository.ApplicationUserRepository;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.BusinessDayConventionRepository;
import com.technicalchallenge.repository.CashflowRepository;
import com.technicalchallenge.repository.CounterpartyRepository;
import com.technicalchallenge.repository.CurrencyRepository;
import com.technicalchallenge.repository.HolidayCalendarRepository;
import com.technicalchallenge.repository.LegTypeRepository;
import com.technicalchallenge.repository.PayRecRepository;
import com.technicalchallenge.repository.ScheduleRepository;
import com.technicalchallenge.repository.TradeLegRepository;
import com.technicalchallenge.repository.TradeRepository;
import com.technicalchallenge.repository.TradeStatusRepository;
import com.technicalchallenge.validation.TradeValidator;
import com.technicalchallenge.validation.ValidationResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private ScheduleRepository scheduleRepository;

    @Mock
    private TradeStatusRepository tradeStatusRepository;

    @Mock
    private LegTypeRepository legTypeRepository;

    @Mock
    private PayRecRepository payRecRepository;

    @Mock
    private BusinessDayConventionRepository businessDayConventionRepository;

    @Mock
    private HolidayCalendarRepository HolidayCalendarRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private AdditionalInfoService additionalInfoService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CounterpartyRepository counterpartyRepository;

    @Mock
    private ApplicationUserRepository applicationUserRepository;

    @Mock
    private BookService bookService;

    @Mock
    private TradeValidator tradeValidator;

    @InjectMocks
    private TradeService tradeService;

    private TradeDTO tradeDTO;
    private Trade trade;
    private Book book;
    private Counterparty counterparty;
    private TradeStatus tradeStatus;
    private TradeLeg tradeLeg;
    private TradeLeg tradeleg1;
    private TradeLeg tradeleg2;
    private TradeLegDTO leg1;
    private TradeLegDTO leg2;
    private List<Cashflow> cashflowList1;
    private List<Cashflow> cashflowList2;
    private Cashflow cashflow1;
    private Cashflow cashflow2;
    private Schedule schedule;
    private ApplicationUser tradeUser1;
    private ApplicationUser tradeUser2;

    @BeforeEach
    void setUp() {
        // Set up test data

        // TraderUser Reference
        tradeUser1 = new ApplicationUser();
        tradeUser1.setActive(true);
        tradeUser1.setId(1L);
        tradeUser1.setFirstName("John");
        tradeUser1.setLastName("Smith");

        tradeUser2 = new ApplicationUser();
        tradeUser2.setId(3L);
        tradeUser2.setFirstName("Jess");
        tradeUser2.setLastName("Abraham");

        // Book Reference
        book = new Book();
        book.setActive(true);
        book.setId(5L);
        book.setBookName("TestBookC");

        // Counterparty Reference
        counterparty = new Counterparty();
        counterparty.setActive(true);
        counterparty.setId(7L);
        counterparty.setName("TestCounterpartyC");

        // Trade Status Reference
        tradeStatus = new TradeStatus();
        tradeStatus.setId(9L);
        tradeStatus.setTradeStatus("NEW");

        // Schedule Reference
        schedule = new Schedule();
        schedule.setId(1L);
        schedule.setSchedule("1M");

        // Trade Leg Reference
        tradeLeg = new TradeLeg();
        tradeLeg.setLegId(5L);
        tradeLeg.setNotional(BigDecimal.valueOf(1000000));
        tradeLeg.setRate(0.05);
        tradeLeg.setCalculationPeriodSchedule(schedule);

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

        tradeleg1 = new TradeLeg();
        tradeleg1.setLegId(1L);
        tradeleg1.setNotional(BigDecimal.valueOf(1000000));
        tradeleg1.setRate(0.05);
        tradeleg1.setCalculationPeriodSchedule(schedule);

        tradeleg2 = new TradeLeg();
        tradeleg2.setLegId(2L);
        tradeleg2.setNotional(BigDecimal.valueOf(1000000));
        tradeleg2.setRate(0.05);
        tradeleg2.setCalculationPeriodSchedule(schedule);

        // Cashflow Reference

        // Cashflow List
        cashflowList1 = new ArrayList<Cashflow>();
        cashflowList2 = new ArrayList<Cashflow>();

        // Cashflow Entity
        cashflow1 = new Cashflow();
        cashflow2 = new Cashflow();

        cashflow1.setTradeLeg(tradeleg1);
        cashflow2.setTradeLeg(tradeleg2);

        // Assigned TradeLegs and Cashflows
        tradeleg1.setCashflows(cashflowList1);
        tradeleg2.setCashflows(cashflowList2);

        // TradeDTO - DTO
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(100001L);
        tradeDTO.setVersion(1);
        tradeDTO.setActive(true);
        tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
        tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
        tradeDTO.setTradeStatusId(tradeStatus.getId());
        tradeDTO.setTradeStatus(tradeStatus.getTradeStatus());
        tradeDTO.setCounterpartyId(counterparty.getId());
        tradeDTO.setCounterpartyName(counterparty.getName());
        tradeDTO.setBookId(book.getId());
        tradeDTO.setBookName(book.getBookName());

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

        trade.setTradeLegs(List.of(tradeleg2, tradeleg2));
        tradeDTO.setTradeLegs(List.of(leg1, leg2));

    }

    // ! WILL IMPLEMENT ! for most tests to clean up and reduce amount of code !
    private void missingMockedStubs() {
        when(bookRepository.findByBookName("TestBookC")).thenReturn(Optional.of(book));
        when(counterpartyRepository.findByName("TestCounterpartyC")).thenReturn(Optional.of(counterparty));
        when(tradeStatusRepository.findByTradeStatus("NEW"))
                .thenReturn(Optional.of(tradeStatus));

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

    /**
     * Tests the Cashflow Generation Monthly Schedule
     */
    @Test
    void testCashflowGeneration_MonthlySchedule() {

        // Given - Setup the two entities for the tradeLegs and assigned seperate
        // cashflow and cashflow lists in the setUp(). Added the schedule repository to
        // define the months interval.

        // Mocked populating the book, counterparty and tradeStatus
        when(bookRepository.findByBookName("TestBookC")).thenReturn(Optional.of(book));
        when(counterpartyRepository.findByName("TestCounterpartyC")).thenReturn(Optional.of(counterparty));
        when(tradeStatusRepository.findByTradeStatus("NEW"))
                .thenReturn(Optional.of(tradeStatus));

        // Mocked saving a new trade
        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);

        // Mocked saving the two different tradeLegs
        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(tradeleg1, tradeleg2);

        // Mocked saving any cashflows and cashflow lists recieved
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if the trade has been created with the trade legs. The Trade
        // leg model has been modified to create new ArrayList<Cashflow>(); and
        // leg.getCashflows().add(cashflow); has been added to the for loop in the
        // generateCashflows() method in the TradeService.class
        Trade result = tradeService.createTrade(tradeDTO);

        // Then - Assertions were replaced to verify the CashflowGeneration is working.
        assertNotNull(result);// The trade is not null
        assertEquals(2, result.getTradeLegs().size()); // Trade has two trade legs
        assertEquals(12, tradeleg1.getCashflows().size()); // tradeleg1 has 12 cashflows
        assertEquals(12, tradeleg2.getCashflows().size()); // tradeleg2 has 12 cashflows
        verify(cashflowRepository, times(24)).save(any(Cashflow.class)); // The Cashflow Repository iterates 24 times.
                                                                         // 12 cashflows for each tradeleg.

    }

    /**
     * Tests if searching for trades by bookName is successful
     */
    @Test
    void testGetTradesByBook_Success() {
        // Given - Mocked the searchTradeByCriteriaDTO and the repository and assigned
        // the trades with a
        // book.

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);
        trade2.setTradeId(100002L);
        trade2.setTradeStartDate(tradeDTO.getTradeStartDate());
        trade2.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());
        trade2.setBook(book);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);
        trade3.setTradeId(100003L);
        trade3.setTradeStartDate(tradeDTO.getTradeStartDate());
        trade3.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());
        trade3.setBook(book);

        SearchTradeByCriteria bookSearch = new SearchTradeByCriteria("TestBookC", null, null, null, null, null, null,
                null, null);

        // Mocked finding all trades based on the Specification - <Specification<Trade>
        when(tradeRepository.findAll(ArgumentMatchers.<Specification<Trade>>any())).thenReturn(List.of(trade2, trade3));

        // When - Uses the method from the service to check if the trades with the same
        // "bookName" have
        // been found.
        List<Trade> result = tradeService.getAllTradesByCriteria(bookSearch);

        // Then - Verifies two result are returned and the search matches the bookName.
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getBook().getBookName().equals("TestBookC")));
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any());

    }

    /**
     * Tests if searching for trades by multiple criteria is successful
     */
    @Test
    void testGetTradesByMultipleCriteria_Success() {
        // Given - Mocked the searchTradeByCriteriaDTO and the repository and assigned
        // the trades with a book, traderUser and counterparty.

        // New Book
        Book book2 = new Book();
        book2.setBookName("TestBookA");

        // New Counterparty
        Counterparty counterparty2 = new Counterparty();
        counterparty2.setName("TestCounterpartyA");

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);
        trade2.setTradeId(100002L);
        trade2.setTradeStartDate(tradeDTO.getTradeStartDate());
        trade2.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());
        trade2.setBook(book2);
        trade2.setTraderUser(tradeUser1);
        trade2.setCounterparty(counterparty2);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);
        trade3.setTradeId(100003L);
        trade3.setTradeStartDate(tradeDTO.getTradeStartDate());
        trade3.setTradeMaturityDate(tradeDTO.getTradeMaturityDate());
        trade3.setBook(book);
        trade3.setTraderUser(tradeUser2);
        trade3.setCounterparty(counterparty);

        // bookName, counterpartyName and traderUserFirstName was used to search
        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria("TestBookA", "TestCounterpartyA", "John", null,
                null, null, null,
                null, null);

        // Mocked finding the trade based on the Specification - <Specification<Trade>
        when(tradeRepository.findAll(ArgumentMatchers.<Specification<Trade>>any()))
                .thenReturn(List.of(trade2));

        // When - Uses the method from the service to check if the trades with same
        // "bookName", "counterpartyName" and "tradeUserFirstName" has been found.
        List<Trade> result = tradeService.getAllTradesByCriteria(criteriaSearch);

        // Then - Verifies one result is returned and the search matches the criteria.
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(t -> t.getBook().getBookName().equals("TestBookA") && t.getCounterparty()
                .getName().equals("TestCounterpartyA") && t.getTraderUser().getFirstName().equals("John")));
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any());

    }

    /**
     * Tests if invalid date range fails when searching for trades
     */
    @Test
    void testGetTradesByInvalidDateRange_ShouldFail() {

        // Given - Invalid tradeStartDate (3/10/2025) and tradeEndDate (3/5/2025) was
        // used to search for all trades with tradeDate within the date range
        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria(null, null, null, null,
                null, null, null,
                LocalDate.of(2025, 10, 3), LocalDate.of(2025, 5, 3));

        // When - A InvalidSearchCriteriaException is thrown and assertThrows returns
        // the exception
        InvalidSearchCriteriaException invalidSearchCriteriaException = assertThrows(
                InvalidSearchCriteriaException.class,
                () -> {
                    tradeService.getAllTradesByCriteria(criteriaSearch);
                });

        // Then - Verifies the exception was thrown.
        assertEquals("End date cannot be before start date", invalidSearchCriteriaException.getMessage());

    }

    /**
     * Tests if finding trades for a specific counterparty by RSQL is successful.
     * Using the RSQL JPA Spring Boot Starter plugin
     */
    @Test
    void testGetTradesByRSQL_Success() {
        // Given - Created the query, new trade entities and assigned
        // the trades with a
        // counterparty and mocked the repository.
        String query = "query=counterparty.name==TestCounterpartyC";

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);
        trade2.setTradeId(100002L);
        trade2.setCounterparty(counterparty);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);
        trade3.setTradeId(100003L);
        trade3.setCounterparty(counterparty);

        // Mocked finding all trades based on the Specification - <Specification<Trade>
        when(tradeRepository.findAll(ArgumentMatchers.<Specification<Trade>>any()))
                .thenReturn(List.of(trade2, trade3));

        // When - Uses the rsql method call from the service to check if the trades with
        // same "counterpartyName" have been found.
        List<Trade> result = tradeService.getAllTradesByRSQL(query);

        // Then - Verifies that 2 trades were found and the search matches the
        // counterpartyName.
        assertEquals(2, result.size());
        assertEquals("TestCounterpartyC", result.get(0).getCounterparty().getName());
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any());
    }

    /**
     * Tests when the query is missing or null, a exception is thrown. Using the
     * RSQL JPA Spring Boot Starter plugin
     */
    @Test
    void testGetTradesByRSQL_MissingQuery() {
        // Given - The query is empty
        String query = "";

        // When - A InvalidRsqlQueryException is thrown and assertThrows returns
        // the exception
        InvalidRsqlQueryException invalidRsqlQueryException = assertThrows(
                InvalidRsqlQueryException.class,
                () -> {
                    tradeService.getAllTradesByRSQL(query);
                });

        // Then - Verifies the exception was thrown.
        assertEquals("Query must not be null or empty", invalidRsqlQueryException.getMessage());
    }

    /**
     * Tests if pagination with the filter is successful
     */
    @Test
    void testPagedResultsOfTrades_Success() {
        // Given - List of 3 trade entities, filtered criteria, pagination and sort DTOs

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);

        List<Trade> trades = new ArrayList<>();
        trades.add(trade);
        trades.add(trade2);
        trades.add(trade3);

        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria(null, null, null, null, null, null, null, null,
                null);
        PaginationDTO pagination = new PaginationDTO(2, 3);
        SortDTO sortField = new SortDTO(null, null);

        // Mocked repository, page and pageable without sort
        Pageable pageable = PageRequest.of(pagination.pageNo() - 1, pagination.pageSize());
        Page<Trade> mockPage = new PageImpl<>(trades, pageable, trades.size());
        when(tradeRepository.findAll(ArgumentMatchers
                .<Specification<Trade>>any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When - Pagniated Filter method call
        Page<Trade> result = tradeService.getAllTrades(criteriaSearch, pagination, sortField);

        // Then - Verifies that pagination is working seperately
        assertEquals(3, result.getContent().size()); // 3
        assertEquals(2, result.getTotalPages()); // 2 pages
        assertTrue(result.getPageable().isPaged()); // true
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any(), any(Pageable.class));
    }

    /**
     * Tests if pagination and sorting with the filter is successful
     */
    @Test
    void testPagedAndSortedResultsOfTrades_Success() {
        // Given - List of 3 trade entities, filtered criteria, pagination and sort DTOs

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);

        List<Trade> trades = new ArrayList<>();
        trades.add(trade3);
        trades.add(trade2);
        trades.add(trade);

        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria(null, null, null, null, null, null, null, null,
                null);
        PaginationDTO pagination = new PaginationDTO(1, 4);
        SortDTO sortField = new SortDTO("id", "desc");

        // Mocked Pageable with sort, page and repository
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(pagination.pageNo() - 1, pagination
                .pageSize(),
                sort);

        Page<Trade> mockPage = new PageImpl<>(trades, pageable, trades.size());
        when(tradeRepository.findAll(ArgumentMatchers
                .<Specification<Trade>>any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When - Pagniated Filter method call
        Page<Trade> result = tradeService.getAllTrades(criteriaSearch, pagination, sortField);

        // Then - Verifies that both sort and pagination is working
        assertTrue(result.getSort().isSorted()); // true
        assertEquals(3, result.getContent().size()); // 3 results
        assertEquals(3L, result.getContent().get(0).getId()); // desc order
        assertTrue(result.getPageable().isPaged()); // true
        assertEquals(1, result.getTotalPages()); // 1 page
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any(), any(Pageable.class));
    }

    /**
     * Tests if combining the filtered search, pagination and sorting with the
     * filter is successful
     */
    @Test
    void testCombinitationOfFilteredSearchPaginationAndSortingForResultsOfTrades_Success() {
        // Given - List of 3 trade entities, ciltered criteria, pagination and sort DTOs

        Book book2 = new Book();
        book2.setBookName("TestBookA");

        // New Trade Two
        Trade trade2 = new Trade();
        trade2.setId(2L);
        trade2.setTradeId(1002L);
        trade2.setBook(book2);

        // New Trade Three
        Trade trade3 = new Trade();
        trade3.setId(3L);
        trade3.setTradeId(1003L);
        trade3.setBook(book2);

        List<Trade> trades = new ArrayList<>();
        trades.add(trade3);
        trades.add(trade2);

        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria("TestBookA", null, null, null,
                null, null, null,
                null, null);
        PaginationDTO pagination = new PaginationDTO(2, 3);
        SortDTO sortField = new SortDTO("tradeId", "desc");

        // Mocked Pageable with sort, page and repository
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(pagination.pageNo() - 1, pagination
                .pageSize(),
                sort);

        Page<Trade> mockPage = new PageImpl<>(trades, pageable, trades.size());
        when(tradeRepository.findAll(ArgumentMatchers
                .<Specification<Trade>>any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When - Pagniated Filter method call
        Page<Trade> result = tradeService.getAllTrades(criteriaSearch, pagination, sortField);

        // Then - Verifies that filter, sort and pagination work together
        assertEquals(2, result.getContent().size()); // 2 results
        assertEquals(1003L, result.getContent().get(0).getTradeId()); // trade3 is first (desc order)
        assertEquals("TestBookA", result.getContent().get(1).getBook().getBookName()); // trade2 bookName is "TestBookA"
        assertEquals(2, result.getTotalPages()); // 2 pages
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any(), any(Pageable.class));
    }

    /**
     * Tests if filtered, pagination and sorting returns empty page when no results
     * are found
     */
    @Test
    void testPaginatedFilter_EmptyPage() {
        // Given - Filtered Criteria, pagination and sort DTOs
        SearchTradeByCriteria criteriaSearch = new SearchTradeByCriteria(null, "TestCounterpartyA", null, null,
                null, null, null,
                null, null);
        PaginationDTO pagination = new PaginationDTO(1, 6);
        SortDTO sortField = new SortDTO("tradeId", "desc");

        // Mocked Pageable with sort, page and repository
        Sort sort = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(pagination.pageNo() - 1, pagination
                .pageSize(),
                sort);

        Page<Trade> mockPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(tradeRepository.findAll(ArgumentMatchers
                .<Specification<Trade>>any(), any(Pageable.class)))
                .thenReturn(mockPage);

        // When - Pagniated Filter method call
        Page<Trade> result = tradeService.getAllTrades(criteriaSearch, pagination, sortField);

        // Then - Verifies that a empty page is returned
        assertNotNull(result); // result is not null
        assertTrue(result.getContent().isEmpty()); // no results
        assertEquals(0, result.getTotalElements()); // 0 trades
        verify(tradeRepository, times(1)).findAll(ArgumentMatchers.<Specification<Trade>>any(), any(Pageable.class));
    }

    /**
     * Tests if business rules validation validates
     */
    @Test
    void testCreateTradeValidationResult_Success() {

        // Given - Validation Result & mocked both validation methods, saving a
        // trade and tradelegs
        missingMockedStubs();

        ValidationResult validResult = ValidationResult.isValid();

        when(tradeValidator.validateTradeBusinessRules(tradeDTO)).thenReturn(validResult);
        when(tradeValidator.validateTradeLegConsistency(tradeDTO.getTradeLegs())).thenReturn(validResult);

        when(tradeRepository.save(any(Trade.class))).thenReturn(trade);
        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(tradeleg1, tradeleg2);

        // When - createTrade method call
        tradeService.populateReferenceDataByName(trade, tradeDTO);
        Trade result = tradeService.createTrade(tradeDTO);

        // Then - Verifies the trade was created and there was no errors
        assertNotNull(result);
        verify(tradeValidator).validateTradeBusinessRules(tradeDTO);
        verify(tradeValidator).validateTradeLegConsistency(tradeDTO.getTradeLegs());

    }

    /**
     * Tests if business rules validation throws a exception
     */
    @Test
    void testCreateTradeValidationBusinessRules_ShouldFail() {

        // Given - List of errors, validation result & mocked stubbing
        List<String> errors = new ArrayList<>();
        errors.add("Start date cannot be before trade date");

        ValidationResult inValidResult = ValidationResult.isNotValid(errors);
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

    /**
     * Tests if cross leg validation throws a exception
     */
    @Test
    void testCreateTradeCrossLegValidation_ShouldFail() {

        // Given - List of errors, validation result & mocked stubbing

        List<String> errors = new ArrayList<>();
        errors.add("Floating legs must have an index specified");
        errors.add("Fixed legs must have a valid rate");

        ValidationResult validResult = ValidationResult.isValid();
        ValidationResult inValidResult = ValidationResult.isNotValid(errors);

        // Mocked validation of both validation results one has valid results and one
        // has invalid results
        when(tradeValidator.validateTradeBusinessRules(tradeDTO)).thenReturn(validResult);
        when(tradeValidator.validateTradeLegConsistency(tradeDTO.getTradeLegs())).thenReturn(inValidResult);

        // When - A ValidationException is thrown and assertThrows returns the
        // exceptions
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tradeService.createTrade(tradeDTO);
        });

        // Then - Verifies the exception was thrown
        assertEquals("Floating legs must have an index specified", exception.getErrors().get(0));
        assertEquals("Fixed legs must have a valid rate", exception.getErrors().get(1));
        assertEquals(2, exception.getErrors().size());
        verify(tradeRepository, never()).save(any());

    }

}

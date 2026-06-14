package com.technicalchallenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.ValidationException;
import com.technicalchallenge.mapper.CashflowMapper;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.repository.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeLegServiceTest {

    @Mock
    private TradeLegRepository tradeLegRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private LegTypeRepository legTypeRepository;

    @Mock
    private IndexRepository indexRepository;

    @Mock
    private HolidayCalendarRepository holidayCalendarRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private BusinessDayConventionRepository businessDayConventionRepository;

    @Mock
    private PayRecRepository payRecRepository;

    @InjectMocks
    private TradeLegService tradeLegService;

    @Mock
    private TradeValidator tradeValidator;

    @Mock
    private ReferenceDataValidator referenceDataValidator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TradeMapper tradeMapper;

    @Mock
    private TradeLegMapper tradeLegMapper;

    @Mock
    private CashflowMapper cashflowMapper;

    private TradeLeg tradeLeg1;
    private TradeLeg tradeLeg2;
    private List<TradeLeg> tradeLegList1, tradeLegList2;
    private Trade trade;
    private Currency currency;
    private LegType legType;
    private TradeDTO tradeDTO;
    private Book book;
    private Counterparty counterparty;
    private TradeStatus tradeStatus;
    private TradeLegDTO leg1, leg2;
    private List<TradeLegDTO> tradeLegDTOList;
    private List<Cashflow> cashflowList1, cashflowList2;
    private Cashflow cashflow1, cashflow2;
    private Schedule schedule;
    private ApplicationUser traderUser, inputterUser;
    private TradeType tradeType;
    private TradeSubType tradeSubType;
    private HolidayCalendar holidayCalendar;
    private PayRec pay, rec;
    private Index index;
    private BusinessDayConvention paymentBusinessDayConvention;
    private BusinessDayConvention fixingBusinessDayConvention;
    private ValidationResult inValidResult, validResult;
    private List<String> errors;

    @BeforeEach
    void setUp() {
        // Set up related entities
        tradeMapper = new TradeMapper(tradeLegMapper);
        objectMapper.registerModule(new JavaTimeModule());

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

        index = new Index();
        index.setId(1L);
        index.setIndex("LIBOR");

        pay = new PayRec();
        pay.setId(1L);
        pay.setPayRec("Pay");

        rec = new PayRec();
        rec.setId(2L);
        rec.setPayRec("Recieve");

        // TradeDTO - DTO
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(100001L);
        tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
        tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
        tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
        tradeDTO.setTradeStatusId(tradeStatus.getId());
        tradeDTO.setCounterpartyId(counterparty.getId());
        tradeDTO.setBookId(book.getId());
        tradeDTO.setTradeSubTypeId(tradeSubType.getId());
        tradeDTO.setTradeTypeId(tradeType.getId());
        tradeDTO.setTraderUserName(traderUser.getLoginId());
        tradeDTO.setInputterUserName(inputterUser.getLoginId());

        trade = new Trade();
        trade.setId(1L);
        trade.setTradeId(100001L);
        trade.setTradeDate(LocalDate.of(2025, 1, 15));
        trade.setTradeStartDate(LocalDate.of(2025, 1, 17));
        trade.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
        trade.setTraderUser(traderUser);
        trade.setTradeInputterUser(inputterUser);
        trade.setBook(book);
        trade.setCounterparty(counterparty);
        trade.setTradeStatus(tradeStatus);
        trade.setVersion(1);
        trade.setActive(true);
        trade.setTradeSubType(tradeSubType);
        trade.setTradeType(tradeType);

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
        leg1.setIndexId(1L);
        leg1.setIndexName("LIBOR");

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
        leg2.setIndexId(1L);
        leg2.setIndexName("LIBOR");

        // Set up first TradeLeg
        tradeLeg1 = new TradeLeg();
        tradeLeg1.setLegId(1L);
        tradeLeg1.setTrade(trade);
        tradeLeg1.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLeg1.setRate(0.05);
        tradeLeg1.setCurrency(currency);
        tradeLeg1.setLegRateType(legType);

        // Set up second TradeLeg
        tradeLeg2 = new TradeLeg();
        tradeLeg2.setLegId(2L);
        tradeLeg2.setTrade(trade);
        tradeLeg2.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLeg2.setRate(0.03);
        tradeLeg2.setCurrency(currency);
        tradeLeg2.setLegRateType(legType);

        tradeLegList1 = new ArrayList<>();

        // Set up trade legs list
        tradeLegDTOList = new ArrayList<>();
        tradeLegDTOList.add(leg1);
        tradeLegDTOList.add(leg2);

        tradeLegList1 = new ArrayList<>();
        tradeLegList1.add(tradeLeg1);
        tradeLegList1.add(tradeLeg2);
        trade.setTradeLegs(tradeLegList1);

        tradeLegList2 = new ArrayList<>();
        tradeDTO.setTradeLegs(tradeLegDTOList);

        // TradeLeg Business Validation
        errors = new ArrayList<>();
        validResult = ValidationResult.isValid();
        inValidResult = ValidationResult.isNotValid(errors);

    }

    private void populateReferenceData(TradeLeg tradeLeg, TradeLegDTO tradeLegDTO) {
        when(referenceDataValidator.validateCurrencyReference(tradeLegDTO)).thenReturn(currency);
        when(referenceDataValidator.validateLegTypeReference(tradeLegDTO)).thenReturn(legType);
        when(referenceDataValidator.validateIndexReference(tradeLegDTO)).thenReturn(index);
        when(referenceDataValidator.validateHolidayCalendarReference(tradeLegDTO)).thenReturn(holidayCalendar);
        when(referenceDataValidator.validateScheduleReference(tradeLegDTO)).thenReturn(schedule);
        when(referenceDataValidator.validatePaymentBusinessDayConventionReference(tradeLegDTO))
                .thenReturn(paymentBusinessDayConvention);
        when(referenceDataValidator.validateFixingBusinessDayConventionReference(tradeLegDTO))
                .thenReturn(fixingBusinessDayConvention);

        tradeLeg.setCurrency(currency);
        tradeLeg.setLegRateType(legType);
        tradeLeg.setIndex(index);
        tradeLeg.setHolidayCalendar(holidayCalendar);
        tradeLeg.setCalculationPeriodSchedule(schedule);
        tradeLeg.setPaymentBusinessDayConvention(paymentBusinessDayConvention);
        tradeLeg.setFixingBusinessDayConvention(fixingBusinessDayConvention);
    }

    @Test
    void testGetAllTradeLegs() {
        // Given
        when(tradeLegRepository.findAll()).thenReturn(tradeLegList1);

        // When
        List<TradeLeg> result = tradeLegService.getAllTradeLegs();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(tradeLeg1.getLegId(), result.get(0).getLegId());
        assertEquals(tradeLeg2.getLegId(), result.get(1).getLegId());
        verify(tradeLegRepository).findAll();
    }

    @Test
    void testGetTradeLegById() {
        // Given
        when(tradeLegRepository.findById(1L)).thenReturn(Optional.of(tradeLeg1));

        // When
        Optional<TradeLeg> result = tradeLegService.getTradeLegById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getLegId());
        assertEquals(BigDecimal.valueOf(1000000.0), result.get().getNotional());
        assertEquals(0.05, result.get().getRate());
        assertEquals(currency, result.get().getCurrency());
        assertEquals(legType, result.get().getLegRateType());
        verify(tradeLegRepository).findById(1L);
    }

    @Test
    void testGetTradeLegByNonExistentId() {
        // Given
        when(tradeLegRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<TradeLeg> result = tradeLegService.getTradeLegById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(tradeLegRepository).findById(999L);
    }

    @Test
    void testSaveTradeLeg() {
        // Given
        TradeLeg newTradeLeg = new TradeLeg();
        newTradeLeg.setTrade(trade);
        newTradeLeg.setNotional(BigDecimal.valueOf(2000000.0));
        newTradeLeg.setRate(0.04);
        newTradeLeg.setCurrency(currency);
        newTradeLeg.setLegRateType(legType);

        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(newTradeLeg);

        // When
        TradeLeg savedTradeLeg = tradeLegService.saveTradeLeg(newTradeLeg, leg1);

        // Then
        assertNotNull(savedTradeLeg);
        assertEquals(BigDecimal.valueOf(2000000.0), savedTradeLeg.getNotional());
        assertEquals(0.04, savedTradeLeg.getRate());
        verify(tradeLegRepository).save(newTradeLeg);
    }

    @Test
    void testDeleteTradeLeg() {
        // Given
        Long tradeLegId = 1L;
        doNothing().when(tradeLegRepository).deleteById(tradeLegId);

        // When
        tradeLegService.deleteTradeLeg(tradeLegId);

        // Then
        verify(tradeLegRepository).deleteById(tradeLegId);
    }

    /**
     * Tests if cross leg validation is successful
     */
    @Test
    void testCreateTradeCrossLegValidation_Success() {

        // Given
        trade.setTradeLegs(tradeLegList2);
        when(tradeValidator.validateTradeLegConsistency(tradeDTO.getTradeLegs())).thenReturn(validResult);

        TradeLeg tradeLeg3 = new TradeLeg();
        tradeLeg3.setTrade(trade);
        tradeLeg3.setActive(true);
        tradeLeg3.setCreatedDate(LocalDateTime.now());

        TradeLeg tradeLeg4 = new TradeLeg();
        tradeLeg4.setTrade(trade);
        tradeLeg4.setActive(true);
        tradeLeg4.setCreatedDate(LocalDateTime.now());

        when(tradeLegMapper.toEntity(leg1)).thenReturn(tradeLeg3);
        when(tradeLegMapper.toEntity(leg2)).thenReturn(tradeLeg4);

        tradeLeg3.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLeg3.setRate(0.03);
        tradeLeg4.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLeg4.setRate(0.03);

        populateReferenceData(tradeLeg3, leg1);
        populateReferenceData(tradeLeg4, leg2);

        when(referenceDataValidator.validatePayRecReference(leg1)).thenReturn(pay);
        when(referenceDataValidator.validatePayRecReference(leg2)).thenReturn(rec);
        tradeLeg3.setPayReceiveFlag(pay);
        tradeLeg4.setPayReceiveFlag(rec);

        when(tradeLegRepository.save(any(TradeLeg.class))).thenReturn(tradeLeg3, tradeLeg4);

        tradeLegList2.add(tradeLeg3);
        tradeLegList2.add(tradeLeg4);

        // When
        List<TradeLeg> savedTradeLegs = tradeLegService.createTradeLegs(tradeDTO, trade);

        // Then - Verifies the tradelegs were saved
        assertNotNull(savedTradeLegs);
        assertEquals("USD", savedTradeLegs.get(0).getCurrency().getCurrency());
        assertEquals("Recieve", savedTradeLegs.get(1).getPayReceiveFlag().getPayRec());
        assertEquals(0.03, savedTradeLegs.get(1).getRate());
        verify(tradeLegRepository, times(2)).save(any(TradeLeg.class));

    }

    /**
     * Tests if cross leg validation throws a exception
     */
    @Test
    void testCreateTradeCrossLegValidation_ShouldFail() {

        // Given - List of errors, validation result & mocked stubbing
        errors.add("Floating legs must have an index specified");
        errors.add("Fixed legs must have a valid rate");

        // Mocked validation of both validation results one has valid results and one
        // has invalid results
        when(tradeValidator.validateTradeLegConsistency(tradeDTO.getTradeLegs())).thenReturn(inValidResult);

        // When - A ValidationException is thrown and assertThrows returns the
        // exceptions
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            tradeLegService.createTradeLegs(tradeDTO, trade);
        });

        // Then - Verifies the exception was thrown
        assertEquals("Floating legs must have an index specified", exception.getErrors().get(0));
        assertEquals("Fixed legs must have a valid rate", exception.getErrors().get(1));
        assertEquals(2, exception.getErrors().size());

    }
}

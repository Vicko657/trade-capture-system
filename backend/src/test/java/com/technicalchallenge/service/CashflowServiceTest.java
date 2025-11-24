package com.technicalchallenge.service;

import com.technicalchallenge.calculations.BigDecimalPercentages;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Index;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.repository.CashflowRepository;
import com.technicalchallenge.repository.BusinessDayConventionRepository;
import com.technicalchallenge.repository.LegTypeRepository;
import com.technicalchallenge.repository.PayRecRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CashflowServiceTest {

    @Mock
    private CashflowRepository cashflowRepository;

    @Mock
    private PayRecRepository payRecRepository;

    @Mock
    private LegTypeRepository legTypeRepository;

    @Mock
    private BusinessDayConventionRepository businessDayConventionRepository;

    @Mock
    private BigDecimalPercentages bigDecimalPercentages;

    @InjectMocks
    private CashflowService cashflowService;

    private Cashflow cashflow1;
    private Cashflow cashflow2;
    private List<Cashflow> cashflowList;
    private TradeLeg tradeLeg;
    private PayRec payRec;
    private Schedule yearly, monthly, quarterly;
    private LegType fixed, floating;
    private Index libor, euribor;

    @BeforeEach
    void setUp() {
        // Set up related entities
        tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1L);
        tradeLeg.setNotional(BigDecimal.valueOf(1000000.0));

        payRec = new PayRec();
        payRec.setId(1L);
        payRec.setPayRec("PAY");

        monthly = new Schedule();
        monthly.setId(1L);
        monthly.setSchedule("Monthly");

        quarterly = new Schedule();
        quarterly.setId(2L);
        quarterly.setSchedule("Quarterly");

        yearly = new Schedule();
        yearly.setId(3L);
        yearly.setSchedule("Yearly");

        fixed = new LegType();
        fixed.setId(1L);
        fixed.setType("Fixed");

        floating = new LegType();
        floating.setId(2L);
        floating.setType("Floating");

        libor = new Index();
        libor.setId(1L);
        libor.setIndex("LIBOR");

        euribor = new Index();
        euribor.setId(2L);
        euribor.setIndex("EURIBOR");

        // Set up first Cashflow
        cashflow1 = new Cashflow();
        cashflow1.setId(1L);
        cashflow1.setTradeLeg(tradeLeg); // Fixed: was setLeg
        cashflow1.setPaymentValue(BigDecimal.valueOf(25000.0));
        cashflow1.setValueDate(LocalDate.now().plusMonths(6));
        cashflow1.setPayRec(payRec);
        cashflow1.setRate(0.05);
        cashflow1.setValidityStartDate(LocalDate.now().minusDays(1)); // Fixed: LocalDate instead of LocalDateTime
        cashflow1.setValidityEndDate(null);

        // Set up second Cashflow
        cashflow2 = new Cashflow();
        cashflow2.setId(2L);
        cashflow2.setTradeLeg(tradeLeg); // Fixed: was setLeg
        cashflow2.setPaymentValue(BigDecimal.valueOf(25000.0));
        cashflow2.setValueDate(LocalDate.now().plusYears(1));
        cashflow2.setPayRec(payRec);
        cashflow2.setRate(0.05);
        cashflow2.setValidityStartDate(LocalDate.now().minusDays(1)); // Fixed: LocalDate instead of LocalDateTime
        cashflow2.setValidityEndDate(null);

        // Set up cashflow list
        cashflowList = Arrays.asList(cashflow1, cashflow2);
    }

    @Test
    void testGetAllCashflows() {
        // Given
        when(cashflowRepository.findAll()).thenReturn(cashflowList);

        // When
        List<Cashflow> result = cashflowService.getAllCashflows();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(cashflow1.getId(), result.get(0).getId());
        assertEquals(cashflow2.getId(), result.get(1).getId());
        verify(cashflowRepository).findAll();
    }

    @Test
    void testGetCashflowById() {
        // Given
        when(cashflowRepository.findById(1L)).thenReturn(Optional.of(cashflow1));

        // When
        Optional<Cashflow> result = cashflowService.getCashflowById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(BigDecimal.valueOf(25000.0), result.get().getPaymentValue());
        assertEquals(payRec, result.get().getPayRec());
        verify(cashflowRepository).findById(1L);
    }

    @Test
    void testGetCashflowByNonExistentId() {
        // Given
        when(cashflowRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Cashflow> result = cashflowService.getCashflowById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(cashflowRepository).findById(999L);
    }

    @Test
    void testSaveCashflow() {
        // Given
        Cashflow newCashflow = new Cashflow();
        newCashflow.setTradeLeg(tradeLeg); // Fixed: was setLeg
        newCashflow.setPaymentValue(BigDecimal.valueOf(30000.0));
        newCashflow.setValueDate(LocalDate.now().plusMonths(9));
        newCashflow.setPayRec(payRec);
        newCashflow.setRate(0.04);

        when(cashflowRepository.save(any(Cashflow.class))).thenReturn(newCashflow);

        // When
        Cashflow savedCashflow = cashflowService.saveCashflow(newCashflow);

        // Then
        assertNotNull(savedCashflow);
        assertEquals(BigDecimal.valueOf(30000.0), savedCashflow.getPaymentValue());
        assertEquals(0.04, savedCashflow.getRate());
        verify(cashflowRepository).save(newCashflow);
    }

    @Test
    void testSaveCashflowWithInvalidPaymentValue() {
        // Given
        Cashflow invalidCashflow = new Cashflow();
        invalidCashflow.setTradeLeg(tradeLeg); // Fixed: was setLeg
        invalidCashflow.setPaymentValue(BigDecimal.valueOf(-10000.0)); // Negative value
        invalidCashflow.setValueDate(LocalDate.now().plusMonths(3));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cashflowService.saveCashflow(invalidCashflow)); // Fixed:
                                                                                                           // expression
                                                                                                           // lambda
        verify(cashflowRepository, never()).save(any(Cashflow.class));
    }

    @Test
    void testSaveCashflowWithMissingValueDate() {
        // Given
        Cashflow invalidCashflow = new Cashflow();
        invalidCashflow.setTradeLeg(tradeLeg); // Fixed: was setLeg
        invalidCashflow.setPaymentValue(BigDecimal.valueOf(15000.0));
        invalidCashflow.setValueDate(null); // Missing value date

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cashflowService.saveCashflow(invalidCashflow)); // Fixed:
                                                                                                           // expression
                                                                                                           // lambda
        verify(cashflowRepository, never()).save(any(Cashflow.class));
    }

    @Test
    void testDeleteCashflow() {
        // Given
        Long cashflowId = 1L;
        doNothing().when(cashflowRepository).deleteById(cashflowId);

        // When
        cashflowService.deleteCashflow(cashflowId);

        // Then
        verify(cashflowRepository).deleteById(cashflowId);
    }

    /**
     * Tests if fixed leg returns cashflows with a payment value for a
     * quarterly schedule
     */
    @Test
    void testCashflowGeneration_FixedLeg_Quarterly() {

        // Given - Fixed Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1000L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setRate(3.5);
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setLegRateType(fixed);
        tradeLeg.setCalculationPeriodSchedule(quarterly);

        LocalDate startDate = LocalDate.of(2025, 10, 11);
        LocalDate maturityDate = LocalDate.of(2026, 12, 3);

        // Mocked stubbing converting rate to decimal
        when(bigDecimalPercentages.percentToDecimal(BigDecimal.valueOf(tradeLeg.getRate())))
                .thenReturn(new BigDecimal("0.00035"));

        // Mocked stubbing calculatation of the paymentValue using the notional, rate
        // and month
        when(bigDecimalPercentages.toPercentageOf(tradeLeg.getNotional(), new BigDecimal("0.00035"),
                new BigDecimal("3"))).thenReturn(new BigDecimal("87500.00"));

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and returns 87500.00
        assertNotNull(cashflows);
        assertEquals(4, cashflows.size());
        assertEquals(new BigDecimal("87500.00"), cashflows.get(0).getPaymentValue());
        assertEquals(3.5, cashflows.get(2).getRate());

    }

    /**
     * Tests if floating leg returns cashflows with a zero payment value for a
     * quarterly schedule
     */
    @Test
    void testCashflowGeneration_FloatingLeg_Quarterly() {

        // Given - Floating Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1001L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setRate(null);
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setLegRateType(floating);
        tradeLeg.setCalculationPeriodSchedule(quarterly);
        tradeLeg.setIndex(euribor);

        LocalDate startDate = LocalDate.of(2025, 10, 11);
        LocalDate maturityDate = LocalDate.of(2026, 12, 3);

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and the floating leg returns 0
        assertNotNull(cashflows);
        assertEquals(4, cashflows.size());
        assertEquals(new BigDecimal("0"), cashflows.get(0).getPaymentValue());
        assertEquals("Floating", cashflows.get(3).getPaymentType().getType());

    }

    /**
     * Tests if fixed leg returns cashflows with a payment value for a
     * monthly schedule
     */
    @Test
    void testCashflowGeneration_FixedLeg_Monthly() {

        // Given - Fixed Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1002L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setRate(5.0);
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setLegRateType(fixed);
        tradeLeg.setCalculationPeriodSchedule(monthly);

        LocalDate startDate = LocalDate.of(2025, 01, 17);
        LocalDate maturityDate = LocalDate.of(2026, 01, 17);

        // Mocked stubbing converting rate to decimal
        when(bigDecimalPercentages.percentToDecimal(BigDecimal.valueOf(tradeLeg.getRate())))
                .thenReturn(new BigDecimal("0.0005"));

        // Mocked stubbing calculatation of the paymentValue using the notional, rate
        // and month
        when(bigDecimalPercentages.toPercentageOf(tradeLeg.getNotional(), new BigDecimal("0.0005"),
                new BigDecimal("1"))).thenReturn(new BigDecimal("41666.67"));

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and returns 41666.67
        assertNotNull(cashflows);
        assertEquals(12, cashflows.size());
        assertEquals(new BigDecimal("41666.67"), cashflows.get(0).getPaymentValue());
        assertEquals(5.0, cashflows.get(7).getRate());

    }

    /**
     * Tests if floating leg returns cashflows with a zero payment value for a
     * monthly schedule
     */
    @Test
    void testCashflowGeneration_FloatingLeg_Monthly() {

        // Given - Floating Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1001L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setRate(null);
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setLegRateType(floating);
        tradeLeg.setCalculationPeriodSchedule(monthly);
        tradeLeg.setIndex(euribor);

        LocalDate startDate = LocalDate.of(2025, 01, 17);
        LocalDate maturityDate = LocalDate.of(2026, 01, 17);

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and the floating leg returns 0
        assertNotNull(cashflows);
        assertEquals(12, cashflows.size());
        assertEquals(new BigDecimal("0"), cashflows.get(0).getPaymentValue());
        assertEquals("Floating", cashflows.get(3).getPaymentType().getType());

    }

    /**
     * Tests if fixed leg returns cashflows with a payment value for a yearly
     * schedule
     */
    @Test
    void testCashflowGeneration_FixedLeg_Yearly() {

        // Given - Fixed Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1004L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setRate(2.5);
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setLegRateType(fixed);
        tradeLeg.setCalculationPeriodSchedule(yearly);

        LocalDate startDate = LocalDate.of(2025, 01, 17);
        LocalDate maturityDate = LocalDate.of(2027, 01, 17);

        // Mocked stubbing converting rate to decimal
        when(bigDecimalPercentages.percentToDecimal(BigDecimal.valueOf(tradeLeg.getRate())))
                .thenReturn(new BigDecimal("0.00025"));

        // Mocked stubbing calculatation of the paymentValue using the notional, rate
        // and month
        when(bigDecimalPercentages.toPercentageOf(tradeLeg.getNotional(), new BigDecimal("0.00025"),
                new BigDecimal("12"))).thenReturn(new BigDecimal("250000.00"));

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and returns 250000.00
        assertNotNull(cashflows);
        assertEquals(2, cashflows.size());
        assertEquals(new BigDecimal("250000.00"), cashflows.get(0).getPaymentValue());
        assertEquals(2.5, cashflows.get(0).getRate());

    }

    /**
     * Tests if floating leg returns 2 Cashflows with a payment value of 0
     */
    @Test
    void testCashflowGeneration_FloatingLeg_Yearly() {

        // Given - Floating Tradeleg entity, startDate and maturityDate
        TradeLeg tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1001L);
        tradeLeg.setNotional(new BigDecimal(10000000));
        tradeLeg.setActive(true);
        tradeLeg.setCreatedDate(LocalDateTime.now());
        tradeLeg.setRate(null);
        tradeLeg.setLegRateType(floating);
        tradeLeg.setCalculationPeriodSchedule(yearly);
        tradeLeg.setIndex(euribor);

        LocalDate startDate = LocalDate.of(2025, 01, 17);
        LocalDate maturityDate = LocalDate.of(2027, 01, 17);

        // Mocked stubbing of cashflows saved onto the database
        when(cashflowRepository.save(any(Cashflow.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When - Checks if list of cashflows is created
        List<Cashflow> cashflows = cashflowService.generateCashflows(tradeLeg, startDate, maturityDate);

        // Then - Verifies cashflows were generated and the floating leg returns 0
        assertNotNull(cashflows);
        assertEquals(2, cashflows.size());
        assertEquals(new BigDecimal("0"), cashflows.get(0).getPaymentValue());
        assertEquals("Floating", cashflows.get(1).getPaymentType().getType());

    }
}

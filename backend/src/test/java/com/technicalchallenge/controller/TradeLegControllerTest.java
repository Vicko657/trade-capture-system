package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.TradeLegService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TradeLegController.class)
public class TradeLegControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeLegService tradeLegService;

    @MockBean
    private TradeLegMapper tradeLegMapper;

    @InjectMocks
    private ApplicationUserDetails userDetails;

    private ObjectMapper objectMapper;
    private TradeLegDTO tradeLegDTO;
    private TradeLeg tradeLeg;
    private Currency currency;
    private LegType legType;
    private Trade trade;
    private HolidayCalendar holidayCalendar;
    private Schedule schedule;
    private PayRec payRec;
    private BusinessDayConvention paymentBusinessDayConvention;
    private BusinessDayConvention fixingBusinessDayConvention;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Privilege Reference
        Privilege privilege1 = new Privilege();
        privilege1.setName("READ_TRADE");
        Privilege privilege2 = new Privilege();
        privilege2.setName("CREATE_TRADE");
        Privilege privilege3 = new Privilege();
        privilege3.setName("TERMINATE_TRADE");

        // UserPrivilege Reference
        UserPrivilege userPrivilege1 = new UserPrivilege();
        userPrivilege1.setPrivilege(privilege1);

        UserPrivilege userPrivilege2 = new UserPrivilege();
        userPrivilege2.setPrivilege(privilege2);

        UserPrivilege userPrivilege3 = new UserPrivilege();
        userPrivilege3.setPrivilege(privilege3);

        // Application User Reference
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setId(1L);
        applicationUser.setActive(true);
        applicationUser.setVersion(1);
        applicationUser.setFirstName("John");
        applicationUser.setLastName("Doe");

        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setUserType("TRADER");
        userProfile.setPrivileges(List.of(userPrivilege1));
        userProfile.setPrivileges(List.of(userPrivilege2));
        userProfile.setPrivileges(List.of(userPrivilege3));
        applicationUser.setUserProfile(userProfile);

        // Application User Details Reference
        userDetails = new ApplicationUserDetails(applicationUser);

        // Set up related entities
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

        trade = new Trade();
        trade.setId(1L);
        trade.setTradeId(1001L);

        // Set up TradeLegDTO for testing
        tradeLegDTO = new TradeLegDTO();
        tradeLegDTO.setLegId(1L);
        tradeLegDTO.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLegDTO.setRate(0.05);
        tradeLegDTO.setCurrency("USD");
        tradeLegDTO.setLegType("Fixed");
        tradeLegDTO.setCalculationPeriodSchedule("Quarterly");
        tradeLegDTO.setHolidayCalendar("NY");
        tradeLegDTO.setPaymentBusinessDayConvention("Following");
        tradeLegDTO.setPayReceiveFlag("Pay");
        tradeLegDTO.setFixingBusinessDayConvention("Following");

        // Set up TradeLeg entity for testing
        tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1L);
        tradeLeg.setTrade(trade);
        tradeLeg.setNotional(BigDecimal.valueOf(1000000.0));
        tradeLeg.setRate(0.05);
        tradeLeg.setCurrency(currency);
        tradeLeg.setLegRateType(legType);
        tradeLeg.setCalculationPeriodSchedule(schedule);
        tradeLeg.setHolidayCalendar(holidayCalendar);
        tradeLeg.setPayReceiveFlag(payRec);
        tradeLeg.setFixingBusinessDayConvention(fixingBusinessDayConvention);
        tradeLeg.setPaymentBusinessDayConvention(paymentBusinessDayConvention);

        // Set up default mappings
        when(tradeLegMapper.toDto(any(TradeLeg.class))).thenReturn(tradeLegDTO);
        when(tradeLegMapper.toEntity(any(TradeLegDTO.class))).thenReturn(tradeLeg);

        // Mocked User Authentication for Test (Spring Security)
        ApplicationUserDetails userDetails = new ApplicationUserDetails(applicationUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetAllTradeLegs: 200 OK Response")
    void testGetAllTradeLegs() throws Exception {
        // Given
        List<TradeLeg> tradeLegs = Arrays.asList(tradeLeg);
        when(tradeLegService.getAllTradeLegs()).thenReturn(tradeLegs);

        // When/Then
        mockMvc.perform(get("/api/tradeLegs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].legId", is(1)))
                .andExpect(jsonPath("$[0].notional", is(1000000.0)))
                .andExpect(jsonPath("$[0].currency", is("USD")));

        verify(tradeLegService).getAllTradeLegs();
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetTradeLegById: 200 OK Response")
    void testGetTradeLegById() throws Exception {
        // Given
        when(tradeLegService.getTradeLegById(1L)).thenReturn(Optional.of(tradeLeg));

        // When/Then
        mockMvc.perform(get("/api/tradeLegs/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legId", is(1)))
                .andExpect(jsonPath("$.notional", is(1000000.0)))
                .andExpect(jsonPath("$.currency", is("USD")));

        verify(tradeLegService).getTradeLegById(1L);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetTradeLegByIdNotFound: 404 NOT FOUND")
    void testGetTradeLegByIdNotFound() throws Exception {
        // Given
        when(tradeLegService.getTradeLegById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/tradeLegs/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(tradeLegService).getTradeLegById(999L);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateTradeLeg: 201 CREATED")
    void testCreateTradeLeg() throws Exception {
        // Given
        when(tradeLegService.saveTradeLeg(any(TradeLeg.class), any(TradeLegDTO.class))).thenReturn(tradeLeg);

        // When/Then
        mockMvc.perform(post("/api/tradeLegs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tradeLegDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legId", is(1)))
                .andExpect(jsonPath("$.notional", is(1000000.0)));

        verify(tradeLegService).saveTradeLeg(any(TradeLeg.class), any(TradeLegDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateTradeLeg: 404 BAD REQUEST")
    void testCreateTradeLegValidationFailure_NegativeNotional() throws Exception {
        // Given
        tradeLegDTO.setNotional(BigDecimal.valueOf(-1000000.0));

        // When/Then
        mockMvc.perform(post("/api/tradeLegs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tradeLegDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").value("Notional must be positive"));

        verify(tradeLegService, never()).saveTradeLeg(any(TradeLeg.class), any(TradeLegDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateTradeLeg: 404 BAD REQUEST")
    void testCreateTradeLegValidationFailure_MissingCurrency() throws Exception {
        // Given
        tradeLegDTO.setCurrency(null);

        // When/Then
        mockMvc.perform(post("/api/tradeLegs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tradeLegDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").value("Currency is required"));

        verify(tradeLegService, never()).saveTradeLeg(any(TradeLeg.class), any(TradeLegDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateTradeLeg: 404 BAD REQUEST")
    void testCreateTradeLegValidationFailure_MissingLegType() throws Exception {
        // Given
        tradeLegDTO.setLegType(null);

        // When/Then
        mockMvc.perform(post("/api/tradeLegs")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tradeLegDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").value("legType is required"));

        verify(tradeLegService, never()).saveTradeLeg(any(TradeLeg.class), any(TradeLegDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "TERMINATE_TRADE" })
    @DisplayName("DeleteTradeLeg: 204 NO CONTENT")
    void testDeleteTradeLeg() throws Exception {
        // Given
        doNothing().when(tradeLegService).deleteTradeLeg(1L);

        // When/Then
        mockMvc.perform(delete("/api/tradeLegs/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(tradeLegService).deleteTradeLeg(1L);
    }
}

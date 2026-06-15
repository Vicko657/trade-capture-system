package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.dto.CashflowGenerationRequest;
import com.technicalchallenge.dto.CashflowGenerationRequest.TradeLegDTO;
import com.technicalchallenge.mapper.CashflowMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.CashflowService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CashflowController.class)
public class CashflowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CashflowService cashflowService;

    @MockBean
    private CashflowMapper cashflowMapper;

    @InjectMocks
    private ApplicationUserDetails userDetails;

    private ObjectMapper objectMapper;
    private CashflowDTO cashflowDTO;
    private Cashflow cashflow;
    private TradeLeg tradeLeg;
    private PayRec payRec;
    private TradeLegDTO legDTO1, legDTO2;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Privilege Reference
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
        tradeLeg = new TradeLeg();
        tradeLeg.setLegId(1L);
        tradeLeg.setNotional(BigDecimal.valueOf(1000000.0));

        payRec = new PayRec();
        payRec.setId(1L);
        payRec.setPayRec("PAY");

        // Set up CashflowDTO for testing
        cashflowDTO = new CashflowDTO();
        cashflowDTO.setId(1L);
        cashflowDTO.setPaymentValue(BigDecimal.valueOf(25000.0));
        cashflowDTO.setValueDate(LocalDate.now().plusMonths(6));
        cashflowDTO.setRate(0.05);
        cashflowDTO.setPayRec("PAY");

        // Set up Cashflow entity for testing
        cashflow = new Cashflow();
        cashflow.setId(1L);
        cashflow.setTradeLeg(tradeLeg); // Fixed: was setLeg
        cashflow.setPaymentValue(BigDecimal.valueOf(25000.0));
        cashflow.setValueDate(LocalDate.now().plusMonths(6));
        cashflow.setPayRec(payRec);
        cashflow.setRate(0.05);

        // Set up default mappings
        when(cashflowMapper.toDto(any(Cashflow.class))).thenReturn(cashflowDTO);
        when(cashflowMapper.toEntity(any(CashflowDTO.class))).thenReturn(cashflow);
        doNothing().when(cashflowService).populateReferenceDataByName(any(Cashflow.class), any(CashflowDTO.class));

        // Mocked User Authentication for Test (Spring Security)
        ApplicationUserDetails userDetails = new ApplicationUserDetails(applicationUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetAllCashflows: 200 OK Response")
    void testGetAllCashflows() throws Exception {
        // Given
        List<Cashflow> cashflows = Arrays.asList(cashflow);
        when(cashflowService.getAllCashflows()).thenReturn(cashflows);

        // When/Then
        mockMvc.perform(get("/api/cashflows")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].paymentValue", is(25000.0)))
                .andExpect(jsonPath("$[0].payRec", is("PAY")));

        verify(cashflowService).getAllCashflows();
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetCashflowById: 200 OK Response")
    void testGetCashflowById() throws Exception {
        // Given
        when(cashflowService.getCashflowById(1L)).thenReturn(Optional.of(cashflow));

        // When/Then
        mockMvc.perform(get("/api/cashflows/1")

                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.paymentValue", is(25000.0)))
                .andExpect(jsonPath("$.payRec", is("PAY")));

        verify(cashflowService).getCashflowById(1L);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "READ_TRADE" })
    @DisplayName("GetCashflowById: 404 Not Found")
    void testGetCashflowByIdNotFound() throws Exception {
        // Given
        when(cashflowService.getCashflowById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/cashflows/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cashflowService).getCashflowById(999L);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateCashflow: 201 Created")
    void testCreateCashflow() throws Exception {
        // Given
        when(cashflowService.saveCashflow(any(CashflowDTO.class))).thenReturn(cashflow);

        // When/Then
        mockMvc.perform(post("/api/cashflows")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cashflowDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.paymentValue", is(25000.0)));

        verify(cashflowService).saveCashflow(any(CashflowDTO.class));

    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateCashflow: 400 Bad Request")
    void testCreateCashflowValidationFailure_NegativePaymentValue() throws Exception {
        // Given
        cashflowDTO.setPaymentValue(BigDecimal.valueOf(-5000.0));

        // When/Then
        mockMvc.perform(post("/api/cashflows")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cashflowDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", is(List.of("Cashflow value must be positive"))));

        verify(cashflowService, never()).saveCashflow(any(CashflowDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("CreateCashflow: 400 Bad Request")
    void testCreateCashflowValidationFailure_MissingValueDate() throws Exception {
        // Given
        cashflowDTO.setValueDate(null);

        // When/Then
        mockMvc.perform(post("/api/cashflows")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cashflowDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", is(List.of("Value date is required"))));

        verify(cashflowService, never()).saveCashflow(any(CashflowDTO.class));
    }

    @Test
    @WithMockUser(username = "john", authorities = { "TERMINATE_TRADE" })
    @DisplayName("DeleteCashflow: 204 No Content")
    void testDeleteCashflow() throws Exception {
        // Given
        doNothing().when(cashflowService).deleteCashflow(1L);

        // When/Then
        mockMvc.perform(delete("/api/cashflows/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(cashflowService).deleteCashflow(1L);
    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("GenerateCashflows: 200 OK Response")
    void testGenerateCashflows() throws Exception {
        // Given
        legDTO1 = new TradeLegDTO();
        legDTO1.setNotional(BigDecimal.valueOf(1000000.0));
        legDTO1.setLegType("Fixed");
        legDTO1.setRate(0.05);
        legDTO1.setCalculationPeriodSchedule("3M");
        legDTO1.setPayReceiveFlag("Pay");
        legDTO1.setPaymentBusinessDayConvention("Following");
        legDTO1.setIndex("LIBOR");

        legDTO2 = new TradeLegDTO();
        legDTO2.setNotional(BigDecimal.valueOf(1000000.0));
        legDTO2.setLegType("Floating");
        legDTO2.setRate(0.03);
        legDTO2.setCalculationPeriodSchedule("3M");
        legDTO2.setPayReceiveFlag("Recieve");
        legDTO2.setPaymentBusinessDayConvention("Following");
        legDTO2.setIndex("LIBOR");

        CashflowGenerationRequest request = new CashflowGenerationRequest();
        request.setTradeStartDate(LocalDate.now());
        request.setTradeMaturityDate(LocalDate.now().plusYears(2));
        request.setLegs(Arrays.asList(legDTO1, legDTO2));

        List<CashflowDTO> generatedCashflows = Arrays.asList(cashflowDTO);

        when(cashflowService.generateCashflowsDTOs(request)).thenReturn(generatedCashflows);

        // Mock the controller's behavior for generating cashflows

        // When/Then
        mockMvc.perform(post("/api/cashflows/generate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

    @Test
    @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
    @DisplayName("GenerateCashflows: 400 Bad Request")
    void testGenerateCashflowsWithNoLegs() throws Exception {
        // Given
        CashflowGenerationRequest request = new CashflowGenerationRequest();
        request.setTradeStartDate(LocalDate.now());
        request.setTradeMaturityDate(LocalDate.now().plusYears(2));
        request.setLegs(new ArrayList<>()); // Empty legs list

        // When/Then
        mockMvc.perform(post("/api/cashflows/generate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

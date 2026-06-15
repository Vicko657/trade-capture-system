package com.technicalchallenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.exceptions.referencedata.TradeNotFoundException;
import com.technicalchallenge.mapper.TradeLegMapper;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.BusinessDayConvention;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Currency;
import com.technicalchallenge.model.HolidayCalendar;
import com.technicalchallenge.model.LegType;
import com.technicalchallenge.model.PayRec;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.Schedule;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.model.TradeLeg;
import com.technicalchallenge.model.TradeStatus;
import com.technicalchallenge.model.TradeSubType;
import com.technicalchallenge.model.TradeType;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.TradeService;
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
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TradeService tradeService;

        @MockBean
        private TradeMapper tradeMapper;

        @MockBean
        private TradeLegMapper tradeLegMapper;

        @InjectMocks
        private ApplicationUserDetails userDetails;

        private ObjectMapper objectMapper;
        private TradeDTO tradeDTO;
        private Trade trade;
        private Book book;
        private Counterparty counterparty;
        private TradeStatus tradeStatus;
        private TradeLegDTO tradeLegDTO;
        private TradeLeg tradeLeg;
        private Currency currency;
        private LegType legType;
        private HolidayCalendar holidayCalendar;
        private Schedule schedule;
        private PayRec payRec;
        private BusinessDayConvention paymentBusinessDayConvention;
        private BusinessDayConvention fixingBusinessDayConvention;
        private ApplicationUser tradeUser;
        private ApplicationUser inputterUser;

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
                privilege3.setName("AMEND_TRADE");
                Privilege privilege4 = new Privilege();
                privilege4.setName("CANCEL_TRADE");
                Privilege privilege5 = new Privilege();
                privilege5.setName("TERMINATE_TRADE");

                // UserPrivilege Reference
                UserPrivilege userPrivilege1 = new UserPrivilege();
                userPrivilege1.setPrivilege(privilege1);

                UserPrivilege userPrivilege2 = new UserPrivilege();
                userPrivilege2.setPrivilege(privilege2);

                UserPrivilege userPrivilege3 = new UserPrivilege();
                userPrivilege3.setPrivilege(privilege3);

                UserPrivilege userPrivilege4 = new UserPrivilege();
                userPrivilege4.setPrivilege(privilege3);

                UserPrivilege userPrivilege5 = new UserPrivilege();
                userPrivilege5.setPrivilege(privilege3);

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

                userProfile.setPrivileges(List.of(userPrivilege1));
                userProfile.setPrivileges(List.of(userPrivilege1));

                userProfile.setPrivileges(List.of(userPrivilege2));
                userProfile.setPrivileges(List.of(userPrivilege3));
                userProfile.setPrivileges(List.of(userPrivilege4));
                userProfile.setPrivileges(List.of(userPrivilege5));

                applicationUser.setUserProfile(userProfile);

                // Application User Details Reference
                userDetails = new ApplicationUserDetails(applicationUser);

                // TraderUser Reference
                tradeUser = new ApplicationUser();
                tradeUser.setActive(true);
                tradeUser.setId(1L);
                tradeUser.setFirstName("John");
                tradeUser.setLastName("Smith");

                inputterUser = new ApplicationUser();
                inputterUser.setId(3L);
                inputterUser.setFirstName("Jess");
                inputterUser.setLastName("Abraham");

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

                TradeType tradeType = new TradeType();
                tradeType.setId(1L);
                tradeType.setTradeType("Swap");

                TradeSubType tradeSubType = new TradeSubType();
                tradeSubType.setId(1L);
                tradeSubType.setTradeSubType("IR Swap");

                // Create a sample TradeDTO for testing
                tradeDTO = new TradeDTO();
                tradeDTO.setId(1L);
                tradeDTO.setTradeId(1001L);
                tradeDTO.setVersion(1);
                tradeDTO.setActive(true);
                tradeDTO.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                tradeDTO.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                tradeDTO.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                tradeDTO.setTradeExecutionDate(LocalDate.now());
                tradeDTO.setTradeStatus("LIVE");
                tradeDTO.setBookName("TestBook");
                tradeDTO.setCounterpartyName("TestCounterparty");
                tradeDTO.setTraderUserName("TestTrader");
                tradeDTO.setInputterUserName("TestInputter");
                tradeDTO.setUtiCode("UTI123456789");
                tradeDTO.setTradeType("Swap");
                tradeDTO.setTradeSubType("IR Swap");

                // Create a sample Trade entity for testing
                trade = new Trade();
                trade.setId(1L);
                trade.setTradeId(1001L);
                trade.setVersion(1);
                trade.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                trade.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                trade.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                trade.setTradeExecutionDate(LocalDate.now());
                trade.setTradeStatus(tradeStatus);
                trade.setBook(book);
                trade.setCounterparty(counterparty);
                trade.setTraderUser(tradeUser);
                trade.setTradeInputterUser(inputterUser);
                trade.setUtiCode("UTI123456789");
                trade.setTradeType(tradeType);
                trade.setTradeSubType(tradeSubType);

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

                tradeDTO.setTradeLegs(List.of(tradeLegDTO, tradeLegDTO));
                trade.setTradeLegs(List.of(tradeLeg, tradeLeg));

                // Set up default mappings
                when(tradeMapper.toDto(any(Trade.class))).thenReturn(tradeDTO);
                when(tradeMapper.toEntity(any(TradeDTO.class))).thenReturn(trade);

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
        @DisplayName("GetAllTrade: 200 OK Response")
        void testGetAllTrades() throws Exception {
                // Given
                List<Trade> trades = List.of(trade); // Fixed: use List.of instead of Arrays.asList for single item

                when(tradeService.getAllTrades()).thenReturn(trades);

                // When/Then
                mockMvc.perform(get("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].tradeId", is(1001)))
                                .andExpect(jsonPath("$[0].bookName", is("TestBook")))
                                .andExpect(jsonPath("$[0].counterpartyName", is("TestCounterparty")));

                verify(tradeService).getAllTrades();
        }

        @Test
        @WithMockUser(username = "john", authorities = { "READ_TRADE" })
        @DisplayName("GetTradeById: 200 OK Response")
        void testGetTradeById() throws Exception {
                // Given
                when(tradeService.getTradeById(1001L)).thenReturn(trade);

                // When/Then
                mockMvc.perform(get("/api/trades/1001")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.tradeId", is(1001)))
                                .andExpect(jsonPath("$.bookName", is("TestBook")))
                                .andExpect(jsonPath("$.counterpartyName", is("TestCounterparty")));

                verify(tradeService).getTradeById(1001L);
        }

        @Test
        @WithMockUser(username = "john", authorities = { "READ_TRADE" })
        @DisplayName("GetTradeByIdNotFound: 404 NOT FOUND")
        void testGetTradeByIdNotFound() throws Exception {
                // Given
                when(tradeService.getTradeById(9999L)).thenThrow(new TradeNotFoundException("tradeId", 9999L));

                // When/Then
                mockMvc.perform(get("/api/trades/9999")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());

                verify(tradeService).getTradeById(9999L);
        }

        @Test
        @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
        @DisplayName("CreateTrade: 201 CREATED")
        void testCreateTrade() throws Exception {
                // Given
                when(tradeService.saveTrade(any(Trade.class), any(TradeDTO.class))).thenReturn(trade);
                doNothing().when(tradeService).populateReferenceDataByName(any(Trade.class), any(TradeDTO.class));

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tradeDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tradeId", is(1001)));

                verify(tradeService).saveTrade(any(Trade.class), any(TradeDTO.class));
                verify(tradeService).populateReferenceDataByName(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
        @DisplayName("CreateTradeValidation_MissingTradeDate: 400 Bad Reques")
        void testCreateTradeValidationFailure_MissingTradeDate() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setBookName("TestBook");
                invalidDTO.setCounterpartyName("TestCounterparty");
                invalidDTO.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                invalidDTO.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                invalidDTO.setTradeExecutionDate(LocalDate.now());
                invalidDTO.setTradeStatus("LIVE");
                invalidDTO.setTraderUserName("TestTrader");
                invalidDTO.setInputterUserName("TestInputter");
                invalidDTO.setUtiCode("UTI123456789");
                invalidDTO.setTradeType("Swap");
                invalidDTO.setTradeSubType("IR Swap");
                invalidDTO.setTradeLegs(List.of(tradeLegDTO, tradeLegDTO));
                // Trade date is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.messages").value("Trade date is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
        @DisplayName("CreateTradeValidation__MissingBook: 400 Bad Request")
        void testCreateTradeValidationFailure_MissingBook() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now());
                invalidDTO.setCounterpartyName("TestCounterparty");
                invalidDTO.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                invalidDTO.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                invalidDTO.setTradeExecutionDate(LocalDate.now());
                invalidDTO.setTradeStatus("LIVE");
                invalidDTO.setTraderUserName("TestTrader");
                invalidDTO.setInputterUserName("TestInputter");
                invalidDTO.setUtiCode("UTI123456789");
                invalidDTO.setTradeType("Swap");
                invalidDTO.setTradeSubType("IR Swap");
                invalidDTO.setTradeLegs(List.of(tradeLegDTO, tradeLegDTO));
                // Book name is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.messages").value("Book name is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        @Test
        @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
        @DisplayName("CreateTradeValidation_MissingCounterparty: 400 Bad Request")
        void testCreateTradeValidationFailure_MissingCounterparty() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now());
                invalidDTO.setBookName("TestBook");
                invalidDTO.setTradeStartDate(LocalDate.now().plusDays(2)); // Fixed: correct method name
                invalidDTO.setTradeMaturityDate(LocalDate.now().plusYears(5)); // Fixed: correct method name
                invalidDTO.setTradeExecutionDate(LocalDate.now());
                invalidDTO.setTradeStatus("LIVE");
                invalidDTO.setTraderUserName("TestTrader");
                invalidDTO.setInputterUserName("TestInputter");
                invalidDTO.setUtiCode("UTI123456789");
                invalidDTO.setTradeType("Swap");
                invalidDTO.setTradeSubType("IR Swap");
                invalidDTO.setTradeLegs(List.of(tradeLegDTO, tradeLegDTO));
                // Counterparty is purposely missing

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.messages").value("Counterparty name is required"));

                verify(tradeService, never()).saveTrade(any(Trade.class), any(TradeDTO.class));
        }

        /**
         * Tests that a trade can be amended
         */
        @Test
        @WithMockUser(username = "john", authorities = { "AMEND_TRADE" })
        @DisplayName("UpdateTrade: 200 OK")
        void testUpdateTrade() throws Exception {
                // Given
                Long tradeId = 1001L;
                tradeDTO.setTradeId(tradeId);
                when(tradeService.amendTrade(eq(tradeId), any(TradeDTO.class))).thenReturn(trade); // Mocked amendTrade
                                                                                                   // stubbing
                                                                                                   // statement
                when(tradeMapper.toDto(any(Trade.class))).thenReturn(tradeDTO); // Mocked toDTO trade mapper

                // When/Then
                // set up a PUT request to a test endpoint
                mockMvc.perform(put("/api/trades/{id}", tradeId)
                                .with(csrf())
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON)
                                // expect Mapper used to return tradeDTO
                                .content(objectMapper.writeValueAsString(tradeDTO)))
                                // expect response status 200 OK REQUEST
                                .andExpect(status().isOk())
                                // expect json path tradeId = 1001
                                .andExpect(jsonPath("$.tradeId", is(1001)));
                // Checks if amendTrade mock had happened
                verify(tradeService).amendTrade(eq(tradeId), any(TradeDTO.class));
        }

        /**
         * Tests expected response code when a trade has been deleted
         */
        @Test
        @WithMockUser(username = "john", authorities = { "CANCEL_TRADE" })
        @DisplayName("DeleteTrade: 204 No Content")
        void testDeleteTrade() throws Exception {
                // Given
                doNothing().when(tradeService).deleteTrade(1001L);

                // When/Then
                // set up a DELETE request to a test endpoint
                mockMvc.perform(delete("/api/trades/1001")
                                .with(csrf())
                                // expect JSON to be returned
                                .contentType(MediaType.APPLICATION_JSON))
                                // expect response status 204 NO CONTENT REQUEST
                                .andExpect(status().isNoContent());
                // Checks if trade has been deleted
                verify(tradeService).deleteTrade(1001L);
        }

        @Test
        @WithMockUser(username = "john", authorities = { "CREATE_TRADE" })
        @DisplayName("CreateTradeWithValidationErrors: 400 Bad Request")
        void testCreateTradeWithValidationErrors() throws Exception {
                // Given
                TradeDTO invalidDTO = new TradeDTO();
                invalidDTO.setTradeDate(LocalDate.now()); // Fixed: LocalDate instead of LocalDateTime
                // Missing required fields to trigger validation errors

                // When/Then
                mockMvc.perform(post("/api/trades")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());

                verify(tradeService, never()).createTrade(any(TradeDTO.class));
        }
}

package com.technicalchallenge.controller;

import com.technicalchallenge.dto.CounterpartyDTO;
import com.technicalchallenge.mapper.CounterpartyMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.CounterpartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CounterpartyController.class)
public class CounterpartyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CounterpartyService counterpartyService;

    @MockBean
    private CounterpartyMapper counterpartyMapper;

    @InjectMocks
    private ApplicationUserDetails userDetails;

    @BeforeEach
    public void setup() {
        // Privilege Reference
        Privilege privilege = new Privilege();
        privilege.setName("READ_TRADE");

        // UserPrivilege Reference
        UserPrivilege userPrivilege = new UserPrivilege();
        userPrivilege.setPrivilege(privilege);

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
        userProfile.setPrivileges(List.of(userPrivilege));
        applicationUser.setUserProfile(userProfile);

        // Application User Details Reference
        userDetails = new ApplicationUserDetails(applicationUser);

        Counterparty counterparty = new Counterparty();
        counterparty.setId(1L);
        counterparty.setName("Counterparty 1");
        counterparty.setAddress("Address 1");

        CounterpartyDTO counterpartyDTO = new CounterpartyDTO();
        counterpartyDTO.setId(counterparty.getId());
        counterpartyDTO.setName(counterparty.getName());
        counterpartyDTO.setAddress(counterparty.getAddress());

        when(counterpartyService.getAllCounterparties()).thenReturn(List.of(counterparty));
        when(counterpartyMapper.toDto(counterparty)).thenReturn(counterpartyDTO);
        when(counterpartyMapper.toEntity(counterpartyDTO)).thenReturn(counterparty);

        // Mocked User Authentication for Test (Spring Security)
        ApplicationUserDetails userDetails = new ApplicationUserDetails(applicationUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockUser(username = "john", roles = "TRADER")
    @DisplayName("GetAllCounterparties: 200 OK Response")
    void shouldReturnAllCounterparties() throws Exception {
        mockMvc.perform(get("/api/counterparties"))
                .andExpect(status().isOk());
    }
}

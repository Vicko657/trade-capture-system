package com.technicalchallenge.controller;

import com.technicalchallenge.dto.UserDTO;
import com.technicalchallenge.mapper.ApplicationUserMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.ApplicationUserService;
import com.technicalchallenge.service.UserProfileService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationUserService applicationUserService;
    @MockBean
    private ApplicationUserMapper applicationUserMapper;
    @MockBean
    private UserProfileService userProfileService;

    @InjectMocks
    private ApplicationUserDetails userDetails;

    @BeforeEach
    public void setup() {

        // Privilege Reference
        Privilege privilege = new Privilege();
        privilege.setName("READ_USER");

        // UserPrivilege Reference
        UserPrivilege userPrivilege = new UserPrivilege();
        userPrivilege.setPrivilege(privilege);

        // Application User Reference
        ApplicationUser applicationUser1 = new ApplicationUser();
        applicationUser1.setId(1L);
        applicationUser1.setActive(true);
        applicationUser1.setVersion(1);
        applicationUser1.setFirstName("Alice");
        applicationUser1.setLastName("Swift");

        ApplicationUser applicationUser2 = new ApplicationUser();
        applicationUser2.setId(2L);
        applicationUser2.setActive(true);
        applicationUser2.setVersion(1);
        applicationUser2.setFirstName("John");
        applicationUser2.setLastName("Doe");

        UserProfile userProfile1 = new UserProfile();
        userProfile1.setId(1L);
        userProfile1.setUserType("ADMIN");
        userProfile1.setPrivileges(List.of(userPrivilege));
        applicationUser1.setUserProfile(userProfile1);

        UserProfile userProfile2 = new UserProfile();
        userProfile2.setId(2L);
        userProfile2.setUserType("TRADER");
        applicationUser2.setUserProfile(userProfile2);

        // Application User Details Reference
        userDetails = new ApplicationUserDetails(applicationUser1);

        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setFirstName("Alice");
        userDTO1.setLastName("Swift");
        userDTO1.setActive(true);
        userDTO1.setVersion(1);
        userDTO1.setUserProfile("ADMIN");

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setFirstName("John");
        userDTO2.setLastName("Doe");
        userDTO2.setActive(true);
        userDTO2.setVersion(1);
        userDTO2.setUserProfile("TRADER");

        when(userProfileService.getAllUserProfiles()).thenReturn(List.of(userProfile1, userProfile2));
        when(applicationUserService.getAllUsers()).thenReturn(List.of(applicationUser1, applicationUser2));
        when(applicationUserMapper.toDto(any())).thenReturn(userDTO1, userDTO2);
        when(applicationUserMapper.toEntity(any())).thenReturn(applicationUser1, applicationUser2);

        // Mocked User Authentication for Test (Spring Security)
        ApplicationUserDetails userDetails = new ApplicationUserDetails(applicationUser1);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Test
    @WithMockUser(username = "alice", roles = "ADMIN")
    @DisplayName("GetAllUsers: 200 OK Response")
    void shouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
    // Add more tests for POST, PUT, DELETE as needed
}

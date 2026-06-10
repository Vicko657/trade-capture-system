package com.technicalchallenge.controller;

import com.technicalchallenge.dto.BookDTO;
import com.technicalchallenge.mapper.BookMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Privilege;
import com.technicalchallenge.model.UserPrivilege;
import com.technicalchallenge.model.UserProfile;
import com.technicalchallenge.security.ApplicationUserDetails;
import com.technicalchallenge.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookMapper bookMapper;

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

        Book book = new Book();
        book.setBookName("Book Name");
        book.setId(1L);
        book.setActive(true);
        book.setVersion(1);
        book.setCostCenter(null);

        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookName("Book Name");
        bookDTO.setId(1L);
        bookDTO.setActive(true);
        bookDTO.setVersion(1);

        when(bookService.getAllBooks()).thenReturn(List.of(bookDTO));
        when(bookMapper.toDto(book)).thenReturn(bookDTO);
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Mocked User Authentication for Test (Spring Security)
        ApplicationUserDetails userDetails = new ApplicationUserDetails(applicationUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @Test
    @WithMockUser(username = "john", roles = "TRADER")
    @DisplayName("GetAllCounterparties: 200 OK Response")
    void shouldReturnAllBooks() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

}

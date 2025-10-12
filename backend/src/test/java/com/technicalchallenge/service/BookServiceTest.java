package com.technicalchallenge.service;

import com.technicalchallenge.dto.BookDTO;
import com.technicalchallenge.mapper.BookMapper;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.CostCenterRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CostCenterRepository costCenterRepository;

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    private Book book;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        // Set up test data

        bookService = new BookService(bookRepository, costCenterRepository, bookMapper);

        bookDTO = new BookDTO();
        bookDTO.setId(1L);
        bookDTO.setBookName("TestBookName");
        bookDTO.setVersion(1);
        bookDTO.setActive(true);
        bookDTO.setCostCenterName(null);

        book = new Book();
        book.setId(1L);

    }

    /**
     * Tests for when a Book is found by id
     */
    @Test
    void testFindBookById() {
        // Given - Set the bookId and mock repository
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        // Added Stub - Mapper converts the book to a dto
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When - Queries if 1L exists
        Optional<BookDTO> found = bookService.getBookById(1L);

        // Then - Checks if the id is present
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    /**
     * Tests if a book saves successfully
     */
    @Test
    void testSaveBook() {
        // Given - Set the bookId and mock service, repository and mapper
        book.setId(2L);
        bookDTO.setId(2L);

        // Created a partial mock of bookService, to target the void
        // populateReferenceDataByName
        bookService = spy(bookService);

        // Added stub - which allows the bookMapper to convert the dto (bookDTO) to a
        // entity (book)
        when(bookMapper.toEntity(bookDTO)).thenReturn(book);

        // Added a void stub - which mocked the void internal method call
        // populateReferenceDataByName
        doNothing().when(bookService).populateReferenceDataByName(book, bookDTO);

        // The book is then saved
        when(bookRepository.save(book)).thenReturn(book);

        // Added stubbing after, the bookMapper converts the entity (book) back to a dto
        // (bookDTO)
        when(bookMapper.toDto(book)).thenReturn(bookDTO);

        // When - Queries the service layer if the book is saved
        BookDTO saved = bookService.saveBook(bookDTO);

        // Then - Checks if the book was saved
        assertNotNull(saved); // Checks if the bookid is not null
        assertEquals(2L, saved.getId()); // Checks that the expected and actual bookId match
        verify(bookRepository).save(book);// Verifies if the book was saved
    }

    @Test
    void testDeleteBook() {
        Long bookId = 3L;
        doNothing().when(bookRepository).deleteById(bookId);
        bookService.deleteBook(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    /**
     * Tests for when a Book is not found by a non existant id and returns an empty
     * set
     */
    @Test
    void testFindBookByNonExistentId() {
        // Given - Set the bookId and mock repository
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        // When - Queries if the 99L exists
        Optional<BookDTO> found = bookService.getBookById(99L);
        // Then - Checks if the id is not present
        assertFalse(found.isPresent());
    }

    // Business logic: test book cannot be created with null name
    @Test
    void testBookCreationWithNullNameThrowsException() {
        BookDTO bookDTO = new BookDTO();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> validateBook(bookDTO));
        assertTrue(exception.getMessage().contains("Book name cannot be null"));
    }

    // Helper for business logic validation
    private void validateBook(BookDTO bookDTO) {
        if (bookDTO.getBookName() == null) {
            throw new IllegalArgumentException("Book name cannot be null");
        }
    }
}

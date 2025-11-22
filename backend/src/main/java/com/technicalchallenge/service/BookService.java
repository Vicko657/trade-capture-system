package com.technicalchallenge.service;

import com.technicalchallenge.dto.BookDTO;
import com.technicalchallenge.exceptions.referencedata.BookNotFoundException;
import com.technicalchallenge.mapper.BookMapper;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.CostCenterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final CostCenterRepository costCenterRepository;
    private final BookMapper bookMapper;

    public List<BookDTO> getAllBooks() {
        logger.info("Retrieving all books");
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDTO getBookById(Long id) {
        logger.debug("Retrieving book by id: {}", id);
        Book book = findBookId(id);
        BookDTO bookDTO = bookMapper.toDto(book);
        return bookDTO;
    }

    public BookDTO getBookByBookName(String bookName) {
        logger.debug("Retrieving book by id: {}", bookName);
        Book book = findBookName(bookName);
        return bookMapper.toDto(book);
    }

    public void populateReferenceDataByName(Book book, BookDTO dto) {
        if (dto.getCostCenterName() != null && !dto.getCostCenterName().isBlank()) {
            var costCenter = costCenterRepository.findAll().stream()
                    .filter(c -> c.getCostCenterName().equalsIgnoreCase(dto.getCostCenterName()))
                    .findFirst().orElse(null);
            if (costCenter == null)
                throw new IllegalArgumentException("CostCenter '" + dto.getCostCenterName() + "' does not exist");
            book.setCostCenter(costCenter);
        }
        // If costCenterName is null or blank, do not modify the current costCenter
    }

    public BookDTO saveBook(BookDTO dto) {
        logger.info("Saving book: {}", dto.toString());
        var entity = bookMapper.toEntity(dto);
        logger.debug("Saving book Entity: {}", entity);
        populateReferenceDataByName(entity, dto);
        var saved = bookRepository.save(entity);

        return bookMapper.toDto(saved);
    }

    public void deleteBook(Long id) {
        logger.warn("Deleting book with id: {}", id);
        bookRepository.deleteById(id);
    }

    // Checks the Reference Data for Trade Service
    public Book findBookId(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("bookId", id));
    }

    public Book findBookName(String bookName) {
        return bookRepository.findByBookName(bookName)
                .orElseThrow(() -> new BookNotFoundException("bookName", bookName));
    }
}

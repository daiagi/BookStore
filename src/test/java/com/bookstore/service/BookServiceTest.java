package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

  
    @Test
    void testGetAllBooks() {
        // Arrange
        Book book1 = new Book("Title1", "Author1", "Description1", 10.0, "Category1");
        Book book2 = new Book("Title2", "Author2", "Description2", 15.0, "Category2");
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        // Act
        List<BookDTO> books = bookService.getAllBooks();

        // Assert
        assertEquals(2, books.size());
        assertEquals("Title1", books.get(0).getTitle());
        assertEquals("Title2", books.get(1).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBook() {
        // Arrange
        Book book = new Book("Test Book", "Author", "Description", 10.0, "Category");
        book.setId(1);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act
        BookDTO foundBook = bookService.getBook(1);

        // Assert
        assertEquals("Test Book", foundBook.getTitle());
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testGetBook_NotFound() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        BookDTO foundBook = bookService.getBook(1);

        // Assert
        assertNull(foundBook);
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testCreateBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("Test Book");

        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle("Test Book");

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        BookDTO createdBook = bookService.createBook(bookDTO);

        // Assert
        assertEquals("Test Book", createdBook.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBookPrice() {
        // Arrange
        Book book = new Book("Test Book", "Author", "Description", 10.0, "Category");
        book.setId(1);

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // Act
        BookDTO updatedBook = bookService.updateBookPrice(1, 15.0);

        // Assert
        assertEquals(15.0, updatedBook.getPrice());
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBookPrice_NotFound() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        BookDTO updatedBook = bookService.updateBookPrice(1, 15.0);

        // Assert
        assertNull(updatedBook);
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteBook() {
        // Act
        bookService.deleteBook(1);

        // Assert
        verify(bookRepository, times(1)).deleteById(1);
    }
}

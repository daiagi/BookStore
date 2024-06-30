package com.bookstore.controller;

import com.bookstore.config.SecurityConfig;
import com.bookstore.dto.BookDTO;
import com.bookstore.security.JwtTokenProvider;
import com.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(BookController.class)
@Import({JwtTokenProvider.class, SecurityConfig.class})  // Import necessary configurations
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = standaloneSetup(new BookController(bookService)).build();
    }

    @Test
    void testGetAllBooks() throws Exception {
        // Arrange
        BookDTO book1 = new BookDTO("Title1", "Author1", "Description1", 10.0, "Category1");
        BookDTO book2 = new BookDTO("Title2", "Author2", "Description2", 15.0, "Category2");
        List<BookDTO> books = Arrays.asList(book1, book2);

        when(bookService.getAllBooks()).thenReturn(books);

        // Act and Assert
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));
    }

    @Test
    void testGetBook() throws Exception {
        // Arrange
        BookDTO book = new BookDTO("Test Book", "Author", "Description", 10.0, "Category");

        when(bookService.getBook(1)).thenReturn(book);

        // Act and Assert
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void testCreateBook() throws Exception {
        // Arrange
        BookDTO bookDTO = new BookDTO("Test Book", "Author", "Description", 10.0, "Category");

        when(bookService.createBook(any(BookDTO.class))).thenReturn(bookDTO);

        // Act and Assert
        mockMvc.perform(post("/books")
                .contentType("application/json")
                .content(
                        "{\"title\": \"Test Book\", \"author\": \"Author\", \"description\": \"Description\", \"price\": 10.0, \"category\": \"Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void testUpdateBookPrice() throws Exception {
        // Arrange
        BookDTO updatedBook = new BookDTO("Test Book", "Author", "Description", 15.0, "Category");

        when(bookService.updateBookPrice(1, 15.0)).thenReturn(updatedBook);

        // Act and Assert
        mockMvc.perform(put("/books/1/price")
                .param("price", "15.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(15.0));
    }

    @Test
    void testDeleteBook() throws Exception {
        // Act and Assert
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk());
    }
}

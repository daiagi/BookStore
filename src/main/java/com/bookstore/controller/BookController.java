package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book-store/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // requires authentication: false
    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }
// requires authentication: false
    @GetMapping("/search")
    public List<BookDTO> searchBooks(@RequestParam String q) {
        return bookService.searchBooks(q);
    }

    // requires authentication: false
    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    // requires authentication: true
    // requires roles: ADMIN
    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO book) {
        return bookService.createBook(book);
    }

    // requires authentication: true
    // requires roles: ADMIN
    @PutMapping("/{id}/price")
    public BookDTO updateBookPrice(@PathVariable Long id, @RequestParam double price) {
        return bookService.updateBookPrice(id, price);
    }

    // requires authentication: true
    // requires roles: ADMIN
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}

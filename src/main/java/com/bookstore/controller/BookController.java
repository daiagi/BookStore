package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}/price")
    public BookDTO updateBookPrice(@PathVariable Long id, @RequestParam double price) {
        return bookService.updateBookPrice(id, price);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}

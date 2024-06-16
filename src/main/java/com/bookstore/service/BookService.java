package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public List<BookDTO> searchBooks(String searchTerm) {
        return bookRepository.searchBooks(searchTerm)
                .stream()
                .peek(System.out::println)
                .map(BookDTO::new)
                .toList();
    }

    public BookDTO getBook(Long id) {
        Book retreivedBook = bookRepository.findById(id).orElse(null);
        if (retreivedBook != null) {
            return convertToDTO(retreivedBook);
        }
        return null;
    }

    public BookDTO createBook(BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        bookRepository.save(book);
        return convertToDTO(book);
    }

    public BookDTO updateBookPrice(Long id, double newPrice) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            book.setPrice(newPrice);
            bookRepository.save(book);
            return convertToDTO(book);
        }
        return null;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private Book convertToEntity(BookDTO bookDTO) {
        return new Book(bookDTO.getTitle(), bookDTO.getAuthor(), bookDTO.getDescription(), bookDTO.getPrice(),
                bookDTO.getCategory());
    }

    private BookDTO convertToDTO(Book book) {
        return new BookDTO(book.getTitle(), book.getAuthor(), book.getDescription(), book.getPrice(),
                book.getCategory());
    }
}

package com.example.bookstore.book;

import com.example.bookstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findByIsActiveTrue();
    };

    public Book getBookById(Long bookId) {
        return bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book " + bookId + " not found"));
    }

    public List<Book> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorIdAndIsActiveTrue(authorId);
    }
}

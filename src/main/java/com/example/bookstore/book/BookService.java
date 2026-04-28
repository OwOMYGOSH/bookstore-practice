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

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long bookId, Book newBook) {
        Book existBook = getBookById(bookId);
        existBook.setTitle(newBook.getTitle());
        existBook.setPrice(newBook.getPrice());
        existBook.setStock(newBook.getStock());
        existBook.setAuthor(newBook.getAuthor());
        return bookRepository.save(existBook);
    }

    public Book deleteBook(Long bookId) {
        Book existBook = getBookById(bookId);
        existBook.setActive(false);
        return bookRepository.save(existBook);
    }
}

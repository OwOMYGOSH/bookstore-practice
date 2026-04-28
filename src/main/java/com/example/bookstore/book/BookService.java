package com.example.bookstore.book;

import com.example.bookstore.author.Author;
import com.example.bookstore.author.AuthorRepository;
import com.example.bookstore.book.dto.CreateBookRequest;
import com.example.bookstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findByIsActiveTrue();
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book " + bookId + " not found"));
    }

    public List<Book> getBooksByAuthorId(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author " + authorId + " not found");
        }
        return bookRepository.findByAuthorIdAndIsActiveTrue(authorId);
    }

    public Book addBook(CreateBookRequest request) {
        Book book = new Book();
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author " + request.getAuthorId() + " not found"));

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPrice(request.getPrice());
        book.setStock(request.getStock());
        book.setPublishedAt(request.getPublishedAt());
        book.setAuthor(author);
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

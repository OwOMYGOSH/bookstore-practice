package com.example.bookstore.book;

import com.example.bookstore.author.Author;
import com.example.bookstore.author.AuthorRepository;
import com.example.bookstore.book.dto.BookRequest;
import com.example.bookstore.book.dto.BookResponse;
import com.example.bookstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<BookResponse> getAllBooks() {
        return bookRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public BookResponse getBookById(Long bookId) {
        return toResponse(findBookOrThrow(bookId));
    }

    public List<BookResponse> getBooksByAuthorId(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new ResourceNotFoundException("Author " + authorId + " not found");
        }
        return bookRepository.findByAuthorIdAndIsActiveTrue(authorId).stream()
                .map(this::toResponse)
                .toList();
    }

    public BookResponse addBook(BookRequest request) {
        Book book = new Book();
        Author author = findAuthorOrThrow(request.authorId());

        applyToBook(book, request, author);
        bookRepository.save(book);
        return toResponse(book);
    }

    public BookResponse updateBook(Long bookId, BookRequest request) {
        Book existBook = findBookOrThrow(bookId);
        Author author = findAuthorOrThrow(request.authorId());

        applyToBook(existBook, request, author);
        bookRepository.save(existBook);
        return toResponse(existBook);
    }

    public void deleteBook(Long bookId) {
        Book existBook = findBookOrThrow(bookId);
        existBook.setActive(false);
        bookRepository.save(existBook);
    }

    private Book findBookOrThrow(Long bookId) {
        return bookRepository.findByIdAndIsActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book " + bookId + " not found"));
    }

    private Author findAuthorOrThrow(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author " + authorId + " not found"));
    }

    private void applyToBook(Book book, BookRequest request, Author author) {
        book.setTitle(request.title());
        book.setIsbn(request.isbn());
        book.setPrice(request.price());
        book.setStock(request.stock());
        book.setPublishedAt(request.publishedAt());
        book.setAuthor(author);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPrice(),
                book.getStock(),
                book.getPublishedAt(),
                book.isActive(),
                book.getAuthor().getId(),
                book.getAuthor().getName()
        );
    }
}

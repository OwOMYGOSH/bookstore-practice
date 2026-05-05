package com.example.bookstore.book;

import com.example.bookstore.book.dto.BookRequest;
import com.example.bookstore.book.dto.BookResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookResponse> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("{bookId}")
    public BookResponse getBookById(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse addBook(@Valid @RequestBody BookRequest request) {
        return bookService.addBook(request);
    }

    @PutMapping("{bookId}")
    public BookResponse updateBook(@PathVariable Long bookId, @Valid @RequestBody BookRequest request) {
        return bookService.updateBook(bookId, request);
    }

    @DeleteMapping("{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable Long bookId) {
         bookService.deleteBook(bookId);
    }
}

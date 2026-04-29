package com.example.bookstore.book;

import com.example.bookstore.book.dto.BookRequest;
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
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("{bookId}")
    public Book getBookById(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@Valid @RequestBody BookRequest request) {
        return bookService.addBook(request);
    }

    @PutMapping("{bookId}")
    public Book updateBook(@PathVariable Long bookId, @Valid @RequestBody BookRequest request) {
        return bookService.updateBook(bookId, request);
    }

    @DeleteMapping("{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookById(@PathVariable Long bookId) {
         bookService.deleteBook(bookId);
    }
}
